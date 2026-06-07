package com.example.smartexpensecalendar.sms

import android.content.Context
import android.provider.Telephony
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import com.example.smartexpensecalendar.domain.model.ProcessingStatus
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.utils.NotificationHelper
import com.example.smartexpensecalendar.domain.model.PaymentMethod
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.ZoneId

@HiltWorker
class SMSSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ExpenseRepository,
    private val categorizer: SMSCategorizer,
    private val dataStoreManager: DataStoreManager
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): androidx.work.ForegroundInfo {
        return NotificationHelper.getSyncForegroundInfo(applicationContext, "Scanning SMS messages...")
    }

    override suspend fun doWork(): Result {
        val syncYear = inputData.getInt("sync_year", -1)
        val syncMonth = inputData.getInt("sync_month", -1)
        
        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) {
            // Foreground not supported or failed
        }

        val selection = if (syncYear != -1 && syncMonth != -1) {
            val startOfMonth = java.time.YearMonth.of(syncYear, syncMonth).atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = java.time.YearMonth.of(syncYear, syncMonth).atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            "${Telephony.Sms.DATE} >= $startOfMonth AND ${Telephony.Sms.DATE} <= $endOfMonth"
        } else {
            null
        }

        val contentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            selection,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val idIndex = it.getColumnIndex(Telephony.Sms._ID)

            val totalCount = it.count
            var currentCount = 0
            var processedExpenses = 0

            while (it.moveToNext()) {
                currentCount++
                if (currentCount % 10 == 0 || currentCount == totalCount) {
                    val progress = currentCount.toFloat() / totalCount
                    setProgress(workDataOf(
                        "progress" to progress,
                        "total_read" to currentCount,
                        "expenses_found" to processedExpenses
                    ))
                    
                    try {
                        setForeground(NotificationHelper.getSyncForegroundInfo(
                            applicationContext, 
                            "Processed $currentCount/$totalCount messages..."
                        ))
                    } catch (e: Exception) {}
                }

                val body = it.getString(bodyIndex)
                val address = it.getString(addressIndex)
                val senderInfo =
                    SenderValidationEngine.validate(address)

                val date = it.getLong(dateIndex)
                val id = it.getLong(idIndex)

                // Skip if already processed by ID
                if (repository.isSmsIdProcessed(id)) {
                    continue
                }

                // Additional check for body similarity (optional but helps with cross-device duplicates)
                if (repository.isSMSSimilarProcessed(body)) {
                    // Log it as ignored or skip
                    continue
                }

                val parsed = SMSParser.parse(body)
                if (parsed == null || !parsed.isFinancial) {
                    continue
                }
                
                val finalParsed = parsed.copy(senderType = senderInfo.senderType)
                val normalizedMerchant = finalParsed.merchant

                processedExpenses++
                val category = if (finalParsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.SETTLEMENT)
                    "Settlement"
                else if (finalParsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.REFUNDED)
                    "Refund"
                else {
                    val initialCat = categorizer.categorize(normalizedMerchant)
                    if (initialCat == "Miscellaneous" &&  finalParsed.paymentMethod == PaymentMethod.UPI) {
                        "UPI / Digital"
                    } else {
                        initialCat
                    }
                }

                val localDate = Instant.ofEpochMilli(date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Logical Deduplication: Check if same amount already exists on this day
                if (repository.findSimilarExpense(finalParsed.amount, localDate) != null) {
                    repository.logSMSProcessing(
                        SMSProcessingLog(id, address, body, date, ProcessingStatus.IGNORED)
                    )
                    continue
                }

                // Reconciliation Logic
                var linkedId: Long? = null
                if (finalParsed.type == com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT) {
                    val match = repository.findMatchingExpense(finalParsed.amount, localDate, 3)
                    if (match != null) {
                        linkedId = match.id
                        // Update the original debit to match this status
                        repository.updateExpenseStatus(match.id, finalParsed.status, null) // We'll set linkedId after we get the new ID
                    }
                }

                // Each unique financial SMS is a separate record now
                repository.upsertExpense(
                    Expense(
                        amount = finalParsed.amount,
                        category = category,
                        date = localDate,
                        merchant = normalizedMerchant,
                        financialEventType =
                            finalParsed.financialEventType,
                        paymentMethod =
                            finalParsed.paymentMethod,
                        confidence =
                            finalParsed.confidence,
                        source = ExpenseSource.SMS,
                        type = finalParsed.type,
                        status = finalParsed.status,
                        accountSuffix = finalParsed.accountSuffix,
                        linkedId = linkedId,
                        originalSmsId = id,
                        originalSmsBody = body,
                        syncDate = System.currentTimeMillis()
                    )
                )

                repository.logSMSProcessing(
                    SMSProcessingLog(
                        id, address, body, date, ProcessingStatus.IGNORED, // Use IGNORED for individual logs during bulk sync
                        parsedAmount = finalParsed.amount, parsedMerchant = normalizedMerchant
                    )
                )
            }
            
            if (syncYear != -1 && syncMonth != -1 && processedExpenses > 0) {
                com.example.smartexpensecalendar.utils.NotificationHelper.showSyncCompleteNotification(
                    applicationContext,
                    "${java.time.Month.of(syncMonth).name} $syncYear",
                    processedExpenses
                )

                // Log a sync completion event for the UI notification icon
                repository.logSMSProcessing(
                    SMSProcessingLog(
                        smsId = System.currentTimeMillis(), // Unique ID for the summary log
                        sender = "SYSTEM",
                        body = "Successfully synced $processedExpenses expenses for ${java.time.Month.of(syncMonth).name} $syncYear",
                        date = System.currentTimeMillis(),
                        status = ProcessingStatus.SYNC_COMPLETE,
                        parsedAmount = processedExpenses.toDouble() // Using this field to store count for UI
                    )
                )
            }
        }

        if (syncYear != -1 && syncMonth != -1) {
            dataStoreManager.markMonthAsSynced(java.time.YearMonth.of(syncYear, syncMonth).toString())
        }

        return Result.success()
    }
}
