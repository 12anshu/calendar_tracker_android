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
                if (parsed != null && parsed.isFinancial) {
                    processedExpenses++
                    val category = if (parsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.SETTLEMENT) 
                        "Settlement" 
                    else if (parsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.REFUNDED)
                        "Refund"
                    else {
                        val initialCat = categorizer.categorize(parsed.merchant)
                        if (initialCat == "Miscellaneous" && body.lowercase().contains("upi")) {
                            "UPI / Digital"
                        } else {
                            initialCat
                        }
                    }

                    val localDate = Instant.ofEpochMilli(date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    // Logical Deduplication: Check if same amount already exists on this day
                    if (repository.findSimilarExpense(parsed.amount, localDate) != null) {
                        repository.logSMSProcessing(
                            SMSProcessingLog(id, address, body, date, ProcessingStatus.IGNORED)
                        )
                        continue
                    }

                    // Reconciliation Logic
                    var linkedId: Long? = null
                    if (parsed.type == com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT) {
                        val match = repository.findMatchingExpense(parsed.amount, localDate, 3)
                        if (match != null) {
                            linkedId = match.id
                            // Update the original debit to match this status
                            repository.updateExpenseStatus(match.id, parsed.status, null) // We'll set linkedId after we get the new ID
                        }
                    }

                    // Each unique financial SMS is a separate record now
                    repository.upsertExpense(
                        Expense(
                            amount = parsed.amount,
                            category = category,
                            date = localDate,
                            merchant = parsed.merchant,
                            source = ExpenseSource.SMS,
                            type = parsed.type,
                            status = parsed.status,
                            accountSuffix = parsed.accountSuffix,
                            linkedId = linkedId,
                            originalSmsId = id,
                            originalSmsBody = body,
                            syncDate = System.currentTimeMillis()
                        )
                    )

                    repository.logSMSProcessing(
                        SMSProcessingLog(
                            id, address, body, date, ProcessingStatus.PROCESSED,
                            parsedAmount = parsed.amount, parsedMerchant = parsed.merchant
                        )
                    )
                } else {
                    // Log as ignored to avoid re-parsing
                    repository.logSMSProcessing(
                        SMSProcessingLog(id, address, body, date, ProcessingStatus.IGNORED)
                    )
                }
            }
            
            if (syncYear != -1 && syncMonth != -1 && processedExpenses > 0) {
                com.example.smartexpensecalendar.utils.NotificationHelper.showSyncCompleteNotification(
                    applicationContext,
                    "${java.time.Month.of(syncMonth).name} $syncYear",
                    processedExpenses
                )
            }
        }

        if (syncYear != -1 && syncMonth != -1) {
            dataStoreManager.markMonthAsSynced(java.time.YearMonth.of(syncYear, syncMonth).toString())
        }

        return Result.success()
    }
}
