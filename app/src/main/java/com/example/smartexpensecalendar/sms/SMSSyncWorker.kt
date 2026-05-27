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
    private val dataStoreManager: com.example.smartexpensecalendar.data.local.DataStoreManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val syncYear = inputData.getInt("sync_year", -1)
        val syncMonth = inputData.getInt("sync_month", -1)

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
                    setProgress(workDataOf("progress" to (currentCount.toFloat() / totalCount)))
                }

                val body = it.getString(bodyIndex)
                val address = it.getString(addressIndex)
                val date = it.getLong(dateIndex)
                val id = it.getLong(idIndex)

                // Skip if already processed
                if (repository.isSMSSimilarProcessed(body)) continue

                val parsed = SMSParser.parse(body)
                if (parsed != null && parsed.isFinancial) {
                    processedExpenses++
                    val category = categorizer.categorize(parsed.merchant)
                    val localDate = Instant.ofEpochMilli(date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val existing = repository.getExpenseByCategoryAndDate(category, localDate)
                    if (existing != null) {
                        repository.upsertExpense(existing.copy(amount = existing.amount + parsed.amount))
                    } else {
                        repository.upsertExpense(
                            Expense(
                                amount = parsed.amount,
                                category = category,
                                date = localDate,
                                merchant = parsed.merchant,
                                source = ExpenseSource.SMS,
                                originalSmsId = id
                            )
                        )
                    }

                    repository.logSMSProcessing(
                        SMSProcessingLog(id, address, body, date, ProcessingStatus.PROCESSED)
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
