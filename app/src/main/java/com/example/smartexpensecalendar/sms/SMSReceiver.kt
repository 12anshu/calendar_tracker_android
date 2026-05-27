package com.example.smartexpensecalendar.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import com.example.smartexpensecalendar.domain.model.ProcessingStatus
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class SMSReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ExpenseRepository

    @Inject
    lateinit var categorizer: SMSCategorizer

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.displayMessageBody
                val sender = message.displayOriginatingAddress
                val timestamp = message.timestampMillis

                processSMS(context, body, sender, timestamp)
            }
        }
    }

    private fun processSMS(context: Context?, body: String, sender: String, timestamp: Long) {
        scope.launch {
            try {
                // Check if already processed
                if (repository.isSMSSimilarProcessed(body)) {
                    return@launch
                }

                val parsed = SMSParser.parse(body)
                if (parsed != null && parsed.isFinancial) {
                    val category = categorizer.categorize(parsed.merchant)
                    val date = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    // Merge logic: check if same category and date exists
                    val existing = repository.getExpenseByCategoryAndDate(category, date)
                    if (existing != null) {
                        repository.upsertExpense(
                            existing.copy(
                                amount = existing.amount + parsed.amount
                            )
                        )
                    } else {
                        repository.upsertExpense(
                            Expense(
                                amount = parsed.amount,
                                category = category,
                                date = date,
                                merchant = parsed.merchant,
                                source = ExpenseSource.SMS,
                                originalSmsId = timestamp // Using timestamp as a simple ID for now
                            )
                        )
                    }

                    // Show notification for real-time detected expense
                    context?.let {
                        NotificationHelper.showExpenseNotification(it, parsed.amount, parsed.merchant, category)
                    }

                    repository.logSMSProcessing(
                        SMSProcessingLog(
                            smsId = timestamp,
                            sender = sender,
                            body = body,
                            date = timestamp,
                            status = ProcessingStatus.PROCESSED
                        )
                    )
                } else {
                    repository.logSMSProcessing(
                        SMSProcessingLog(
                            smsId = timestamp,
                            sender = sender,
                            body = body,
                            date = timestamp,
                            status = ProcessingStatus.SKIPPED
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SMSReceiver", "Error processing SMS", e)
                repository.logSMSProcessing(
                    SMSProcessingLog(
                        smsId = timestamp,
                        sender = sender,
                        body = body,
                        date = timestamp,
                        status = ProcessingStatus.FAILED,
                        failureReason = e.message
                    )
                )
            }
        }
    }
}
