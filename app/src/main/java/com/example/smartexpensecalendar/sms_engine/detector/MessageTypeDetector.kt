package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.MessageTypePhrases

class MessageTypeDetector {

    /**
     * Sequential Logic Refactored:
     * 1. Obligation (Reminders) - Must be first.
     * 2. Transaction (Debit/Credit) - Must be second to override balance alerts.
     * 3. Information (Status) - Must be last.
     */
    fun detect(sms: String): MessageTypeDetectionResult {
        val text = sms.uppercase()

        // --- STAGE 1: OBLIGATION (Reminders / Future Actions) ---
        val matchedObligation = MessageTypePhrases.obligationPhrases.filter { text.contains(it) }.toSet()
        if (matchedObligation.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.OBLIGATION,
                confidence = 100,
                scores = mapOf(MessageType.OBLIGATION to 100),
                matchedKeywords = mapOf(MessageType.OBLIGATION to matchedObligation)
            )
        }

        // --- STAGE 2: TRANSACTION WITH DIRECTION ---
        // Priority: DEBIT then CREDIT
        
        val matchedDebit = MessageTypePhrases.transactionDebitPhrases.filter { text.contains(it) }.toSet()
        if (matchedDebit.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.TRANSACTION,
                confidence = 100,
                scores = mapOf(MessageType.TRANSACTION to 100),
                matchedKeywords = mapOf(MessageType.TRANSACTION to matchedDebit),
                detectedDirection = TransactionDirection.DEBIT
            )
        }

        val matchedCredit = MessageTypePhrases.transactionCreditPhrases.filter { text.contains(it) }.toSet()
        if (matchedCredit.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.TRANSACTION,
                confidence = 100,
                scores = mapOf(MessageType.TRANSACTION to 100),
                matchedKeywords = mapOf(MessageType.TRANSACTION to matchedCredit),
                detectedDirection = TransactionDirection.CREDIT
            )
        }

        // --- STAGE 3: INFORMATION (Status / Non-Financial) ---
        // Checked only if no transaction rule matched. 
        // This solves the "Spent... Avl Bal" problem.
        val matchedInfo = MessageTypePhrases.informationPhrases.filter { text.contains(it) }.toSet()
        if (matchedInfo.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.INFORMATION,
                confidence = 100,
                scores = mapOf(MessageType.INFORMATION to 100),
                matchedKeywords = mapOf(MessageType.INFORMATION to matchedInfo)
            )
        }

        // --- STAGE 4: FALLBACK ---
        return MessageTypeDetectionResult(
            messageType = MessageType.UNKNOWN,
            confidence = 0,
            scores = emptyMap(),
            matchedKeywords = emptyMap(),
            detectedDirection = TransactionDirection.UNKNOWN
        )
    }
}
