package com.example.smartexpensecalendar.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
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
import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import java.time.Month

@HiltWorker
class SMSSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ExpenseRepository,
    private val categorizer: SMSCategorizer,
    private val dataStoreManager: DataStoreManager,
    private val linker: com.example.smartexpensecalendar.sms.reconciliation.TransactionLinker
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

        // 1. Permission Check
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS) 
            != PackageManager.PERMISSION_GRANTED) {
            return Result.failure()
        }

        val selection = if (syncYear != -1 && syncMonth != -1) {
            val yearMonth = YearMonth.of(syncYear, syncMonth)
            val startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            "${Telephony.Sms.DATE} >= $startOfMonth AND ${Telephony.Sms.DATE} <= $endOfMonth AND ${Telephony.Sms.TYPE} = 1"
        } else {
            "${Telephony.Sms.TYPE} = 1"
        }

        val contentResolver = applicationContext.contentResolver
        val cursor = try {
            contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE),
                selection,
                null,
                "${Telephony.Sms.DATE} DESC"
            )
        } catch (e: Exception) {
            android.util.Log.e("SMSSyncWorker", "Cursor query failed", e)
            null
        }

        var totalReadCount = 0
        var foundExpensesCount = 0

        cursor?.use {
            val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val idIndex = it.getColumnIndexOrThrow(Telephony.Sms._ID)

            val totalCount = it.count
            
            while (it.moveToNext()) {
                try {
                    totalReadCount++
                    if (totalReadCount % 20 == 0 || totalReadCount == totalCount) {
                        val progress = totalReadCount.toFloat() / totalCount
                        setProgress(workDataOf(
                            "progress" to progress,
                            "total_read" to totalReadCount,
                            "expenses_found" to foundExpensesCount
                        ))
                        
                        try {
                            setForeground(NotificationHelper.getSyncForegroundInfo(
                                applicationContext, 
                                "Scanning $totalReadCount/$totalCount messages..."
                            ))
                        } catch (e: Exception) {}
                    }

                    val body = it.getString(bodyIndex) ?: ""
                    val address = it.getString(addressIndex) ?: "UNKNOWN"
                    val date = it.getLong(dateIndex)
                    val id = it.getLong(idIndex)

                    // 2. Technical Deduplication (ID-based is most reliable for same device)
                    if (repository.isSmsIdProcessed(id)) {
                        continue
                    }

                    val senderInfo = SenderValidationEngine.validate(address)
                    val parsed = SMSParser.parse(body)
                    
                    if (parsed == null) {
                        continue
                    }
                    
                    val finalParsed = parsed.copy(senderType = senderInfo.senderType)
                    
                    // Logic: Hide OBLIGATION/PROMOTIONAL/INFORMATION from Transactions list
                    val isHiddenType = finalParsed.messageType == MessageType.OBLIGATION ||
                                     finalParsed.messageType == MessageType.PROMOTIONAL ||
                                     finalParsed.messageType == MessageType.INFORMATION
                    
                    if (isHiddenType) {
                        repository.logSMSProcessing(
                            SMSProcessingLog(
                                id, address, body, date, ProcessingStatus.PROCESSED,
                                parsedAmount = finalParsed.amount, parsedMerchant = finalParsed.merchant
                            )
                        )
                        continue
                    }

                    val normalizedMerchant = finalParsed.merchant

                    foundExpensesCount++
                    val category = categorizer.categorize(
                        merchant = normalizedMerchant,
                        eventType = finalParsed.financialEventType,
                        status = finalParsed.status,
                        paymentMethod = finalParsed.paymentMethod
                    )

                    val localDate = Instant.ofEpochMilli(date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    // --- SMART DEDUPLICATION (Window-Aware & Quality-Aware) ---
                    val existing = repository.findSimilarExpense(finalParsed.amount, localDate, finalParsed.type, windowDays = 1)
                    if (existing != null) {
                        if (finalParsed.quality > existing.quality) {
                            repository.deleteExpense(existing)
                        } else {
                            continue
                        }
                    }

                    repository.upsertExpense(
                        Expense(
                            amount = finalParsed.amount,
                            category = category,
                            date = localDate,
                            merchant = normalizedMerchant,
                            financialEventType = finalParsed.financialEventType,
                            paymentMethod = finalParsed.paymentMethod,
                            confidence = finalParsed.confidence,
                            source = ExpenseSource.SMS,
                            type = finalParsed.type,
                            status = finalParsed.status,
                            accountSuffix = finalParsed.accountSuffix,
                            accountName = finalParsed.accountName,
                            quality = finalParsed.quality,
                            originalSmsId = id,
                            originalSmsBody = body,
                            syncDate = System.currentTimeMillis()
                        )
                    )

                    repository.logSMSProcessing(
                        SMSProcessingLog(
                            id, address, body, date, ProcessingStatus.PROCESSED,
                            parsedAmount = finalParsed.amount, parsedMerchant = normalizedMerchant
                        )
                    )
                } catch (e: Exception) {
                    android.util.Log.e("SMSSyncWorker", "Failed to process SMS", e)
                }
            }
            
            if (syncYear != -1 && syncMonth != -1) {
                val yearMonth = YearMonth.of(syncYear, syncMonth)
                val monthName = Month.of(syncMonth).name
                
                // Run Reconciliation Linker after sync
                linker.linkTransactions(
                    startDate = yearMonth.atDay(1),
                    endDate = yearMonth.atEndOfMonth()
                )

                if (foundExpensesCount > 0) {
                    NotificationHelper.showSyncCompleteNotification(
                        applicationContext,
                        "$monthName $syncYear",
                        foundExpensesCount
                    )
                }

                repository.logSMSProcessing(
                    SMSProcessingLog(
                        smsId = System.currentTimeMillis(),
                        sender = "SYSTEM",
                        body = "Finished sync for $monthName $syncYear. Found $foundExpensesCount new transactions.",
                        date = System.currentTimeMillis(),
                        status = ProcessingStatus.SYNC_COMPLETE,
                        parsedAmount = foundExpensesCount.toDouble()
                    )
                )
            }
        }

        if (syncYear != -1 && syncMonth != -1) {
            dataStoreManager.markMonthAsSynced(YearMonth.of(syncYear, syncMonth).toString())
        }

        return Result.success(workDataOf(
            "progress" to 1.0f,
            "total_read" to totalReadCount,
            "expenses_found" to foundExpensesCount
        ))
    }
}
