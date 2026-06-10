package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms.config.MessageTypePhrases

class MessageTypeDetector {

    /**
     * Sequential Unified Detection with Currency Agnostic Matching.
     */
    fun detect(sms: String): MessageTypeDetectionResult {
        val text = sms.uppercase()

        // --- STAGE 1: OBLIGATION ---
        val matchedObligation = MessageTypePhrases.obligationPhrases.filter { text.contains(it) }.toSet()
        if (matchedObligation.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.OBLIGATION,
                confidence = 100,
                scores = mapOf(MessageType.OBLIGATION to 100),
                matchedKeywords = mapOf(MessageType.OBLIGATION to matchedObligation)
            )
        }

        // --- STAGE 2: TRANSACTION WITH CURRENCY AGNOSTIC MATCHING ---
        // RULE: Check CREDIT before DEBIT because "Payment Received" contains "Payment"
        
        // 2.1 Check CREDIT
        val matchedCredit = MessageTypePhrases.transactionCreditPhrases.filter { phrase ->
            smartContains(text, phrase)
        }.toSet()
        
        if (matchedCredit.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.TRANSACTION,
                confidence = 100,
                scores = mapOf(MessageType.TRANSACTION to 100),
                matchedKeywords = mapOf(MessageType.TRANSACTION to matchedCredit),
                detectedDirection = TransactionDirection.CREDIT
            )
        }

        // 2.2 Check DEBIT
        val matchedDebit = MessageTypePhrases.transactionDebitPhrases.filter { phrase ->
            smartContains(text, phrase)
        }.toSet()
        
        if (matchedDebit.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.TRANSACTION,
                confidence = 100,
                scores = mapOf(MessageType.TRANSACTION to 100),
                matchedKeywords = mapOf(MessageType.TRANSACTION to matchedDebit),
                detectedDirection = TransactionDirection.DEBIT
            )
        }

        // --- STAGE 3: INFORMATION ---
        val matchedInfo = MessageTypePhrases.informationPhrases.filter { text.contains(it) }.toSet()
        if (matchedInfo.isNotEmpty()) {
            return MessageTypeDetectionResult(
                messageType = MessageType.INFORMATION,
                confidence = 100,
                scores = mapOf(MessageType.INFORMATION to 100),
                matchedKeywords = mapOf(MessageType.INFORMATION to matchedInfo)
            )
        }

        return MessageTypeDetectionResult(
            messageType = MessageType.UNKNOWN,
            confidence = 0,
            scores = emptyMap(),
            matchedKeywords = emptyMap(),
            detectedDirection = TransactionDirection.UNKNOWN
        )
    }

    /**
     * Handles the {CUR} placeholder by converting the phrase into a Proximity Regex.
     * Allows for text (like account numbers or amounts) on either side of the currency symbol.
     * e.g., "SPENT {CUR}" matches "Spent using card Rs. 500"
     */
    private fun smartContains(text: String, phrase: String): Boolean {
        return if (phrase.contains(MessageTypePhrases.CUR_PLACEHOLDER)) {
            val escapedPhrase = phrase.replace(".", "\\.")
            
            // Replace {CUR} with a regex that matches the symbol plus optional proximity around it
            // This allows the amount or account info to be adjacent to the symbol.
            val regexStr = escapedPhrase.replace(
                MessageTypePhrases.CUR_PLACEHOLDER, 
                ".{0,50}" + DetectionConstants.CURRENCY_SYMBOLS + ".{0,50}"
            )
            Regex(regexStr, RegexOption.IGNORE_CASE).containsMatchIn(text)
        } else {
            text.contains(phrase)
        }
    }
}
