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
import com.example.smartexpensecalendar.sms.merchant.MerchantNormalizer
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
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
                val senderInfo =
                    SenderValidationEngine.validate(sender)
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

                val senderInfo =
                    SenderValidationEngine.validate(sender)

                val finalParsed =
                    SMSParser.parse(body)?.copy(
                        senderType = senderInfo.senderType
                    ) ?: return@launch
                if (!finalParsed.isFinancial) {
                    return@launch
                }

                val normalizedMerchant =
                    MerchantNormalizer.normalize(
                        finalParsed.merchant
                    )

                val category = when (finalParsed.status) {
                    com.example.smartexpensecalendar.domain.model.TransactionStatus.SETTLEMENT -> "Settlement"
                    com.example.smartexpensecalendar.domain.model.TransactionStatus.REFUNDED -> "Refund"
                    else -> categorizer.categorize(normalizedMerchant)
                }

                val date = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Logical Deduplication
                if (repository.findSimilarExpense(finalParsed.amount, date) != null) {
                    return@launch
                }

                // Reconciliation Logic for real-time
                var linkedId: Long? = null
                if (finalParsed.type == com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT) {
                    val match = repository.findMatchingExpense(finalParsed.amount, date, 3)
                    if (match != null) {
                        linkedId = match.id
                        repository.updateExpenseStatus(match.id, finalParsed.status, null)
                    }
                }

                repository.upsertExpense(
                    Expense(
                        amount = finalParsed.amount,
                        category = category,
                        date = date,
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
                        originalSmsId = timestamp // Using timestamp as a simple ID for now
                    )
                )

                // Show notification for real-time detected expense (Only for completed debits)
                if (finalParsed.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
                    finalParsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED) {
                    context?.let {
                        NotificationHelper.showExpenseNotification(it, finalParsed.amount, normalizedMerchant, category)
                    }
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
