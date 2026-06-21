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
import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import com.example.smartexpensecalendar.utils.NotificationHelper
import com.example.smartexpensecalendar.sms.reconciliation.TransactionLinker
import com.example.smartexpensecalendar.domain.model.PaymentMethod
import com.example.smartexpensecalendar.sms.reconciliation.duplicate.DuplicateMatcher
import com.example.smartexpensecalendar.sms.reconciliation.duplicate.DuplicateDecision
import com.example.smartexpensecalendar.sms_engine.detector.EntityTypeDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class SMSReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ExpenseRepository

    @Inject
    lateinit var categorizer: SMSCategorizer

    @Inject
    lateinit var linker: TransactionLinker

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.displayMessageBody
                val sender = message.displayOriginatingAddress ?: "UNKNOWN"
                val timestamp = message.timestampMillis

                processSMS(context, body, sender, timestamp)
            }
        }
    }

    private fun processSMS(context: Context?, body: String, sender: String, timestamp: Long) {
        scope.launch {
            try {
                if (repository.isSMSSimilarProcessed(body)) return@launch

                val parsed = SMSParser.parse(body)
                if (parsed == null || !parsed.isFinancial) return@launch
                
                val senderInfo = SenderValidationEngine.validate(sender)
                val finalParsed = parsed.copy(senderType = senderInfo.senderType)

                // Hide non-transaction types from main view
                val isHiddenType = finalParsed.messageType == MessageType.OBLIGATION ||
                                 finalParsed.messageType == MessageType.PROMOTIONAL ||
                                 finalParsed.messageType == MessageType.INFORMATION

                if (isHiddenType) {
                    repository.logSMSProcessing(
                        SMSProcessingLog(timestamp, sender, body, timestamp, ProcessingStatus.PROCESSED)
                    )
                    return@launch
                }

                val normalizedMerchant = finalParsed.merchant
                val category = categorizer.categorize(
                    merchant = normalizedMerchant,
                    eventType = finalParsed.financialEventType,
                    status = finalParsed.status,
                    paymentMethod = finalParsed.paymentMethod
                )

                val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

                val entityType = EntityTypeDetector.detect(
                    merchant = normalizedMerchant,
                    eventType = finalParsed.financialEventType,
                    paymentMethod = finalParsed.paymentMethod
                )

                // --- NEW UPGRADED DUPLICATE DETECTION (Phase 1) ---
                val windowMinutes = getWindowMinutes(finalParsed.paymentMethod, finalParsed.financialEventType)
                val startTime = timestamp - (windowMinutes * 60 * 1000)
                val endTime = timestamp + (windowMinutes * 60 * 1000)

                val candidates = repository.findPotentialDuplicates(finalParsed.amount, finalParsed.type, startTime, endTime)
                
                val incomingExpensePlaceholder = Expense(
                    amount = finalParsed.amount,
                    category = category,
                    date = date,
                    merchant = normalizedMerchant,
                    financialEventType = finalParsed.financialEventType,
                    paymentMethod = finalParsed.paymentMethod,
                    confidence = finalParsed.confidence,
                    source = ExpenseSource.SMS,
                    type = finalParsed.type,
                    status = finalParsed.status,
                    accountSuffix = finalParsed.accountSuffix,
                    accountName = finalParsed.accountName,
                    transactionTime = timestamp,
                    senderId = sender,
                    quality = finalParsed.quality,
                    entityType = entityType
                )

                var isDuplicate = false
                for (existing in candidates) {
                    val matchResult = DuplicateMatcher.calculateMatchScore(incomingExpensePlaceholder, existing)
                    if (matchResult.decision == DuplicateDecision.DUPLICATE) {
                        if (finalParsed.quality > existing.quality) {
                            repository.deleteExpense(existing)
                        } else {
                            isDuplicate = true
                            break
                        }
                    }
                }

                if (isDuplicate) {
                    return@launch
                }

                repository.upsertExpense(incomingExpensePlaceholder.copy(
                    originalSmsId = timestamp
                ))

                // Run Unified Linker (48h window)
                linker.linkTransactions(date.minusDays(2), date.plusDays(2))

                // Notification for actual spends
                if (finalParsed.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
                    finalParsed.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED) {
                    context?.let {
                        NotificationHelper.showExpenseNotification(it, finalParsed.amount, normalizedMerchant, category)
                    }
                }

                repository.logSMSProcessing(
                    SMSProcessingLog(timestamp, sender, body, timestamp, ProcessingStatus.PROCESSED)
                )
            } catch (e: Exception) {
                Log.e("SMSReceiver", "Error processing SMS", e)
            }
        }
    }

    private fun getWindowMinutes(method: com.example.smartexpensecalendar.domain.model.PaymentMethod, eventType: com.example.smartexpensecalendar.domain.model.FinancialEventType): Long {
        return when {
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.UPI -> 10
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.CARD || 
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.CREDIT_CARD || 
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.DEBIT_CARD -> 30
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.IMPS -> 30
            method == com.example.smartexpensecalendar.domain.model.PaymentMethod.NEFT || 
            eventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.TRANSFER -> 120
            else -> 1440 // 1 day
        }
    }
}
