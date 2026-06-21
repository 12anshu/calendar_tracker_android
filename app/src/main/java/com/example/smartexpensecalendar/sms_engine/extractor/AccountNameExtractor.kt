package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import java.util.regex.Pattern

object AccountNameExtractor {

    /**
     * Extracts a human-readable name for the source account/card.
     * Uses dynamic regex generation from atomic building blocks.
     */
    fun extract(body: String, sender: String = ""): String? {
        val upperBody = body.uppercase()
        
        // 1. Resolve Bank Family (Centralized Logic)
        var bank = SenderValidationEngine.resolveFamily(sender) ?: ""
        
        // 2. Identify Product/Instrument Type using Registry Atoms
        val isMeal = DetectionPatterns.INSTRUMENT_MEAL.any { upperBody.contains(it) }
        val isCard = DetectionPatterns.INSTRUMENT_CARD.any { upperBody.contains(it) }
        val isWallet = DetectionPatterns.INSTRUMENT_WALLET.any { upperBody.contains(it) }
        
        val product = when {
            isMeal -> "MEAL CARD"
            isCard -> "CARD"
            isWallet -> "WALLET"
            else -> "A/C"
        }
        
        // 3. Extract Suffix
        val suffix = getSuffix(body) ?: ""
        
        // 4. Fallback: If bank is still unknown, search body using Registry
        if (bank.isBlank()) {
            bank = DetectionPatterns.BANKS.find { upperBody.contains(it) } ?: ""
        }

        if (bank.isBlank() && suffix.isBlank()) return null
        
        return buildString {
            if (bank.isNotBlank()) append(bank + " ")
            if (suffix.isNotBlank()) {
                append("[")
                append(product)
                append(" ")
                append(suffix)
                append("]")
            } else {
                append(product)
            }
        }.uppercase().trim()
    }

    fun getFriendlyBankName(sender: String): String? {
        return SenderValidationEngine.resolveFamily(sender)
    }

    fun getSuffix(body: String): String? {
        // Use the unified bank structure regex to find the suffix
        val match = DetectionPatterns.bankStructureRegex.find(body)
        if (match != null) {
            // Find the last numeric group in the match
            return Regex("\\d{2,4}\\b").find(match.value)?.value
        }
        
        // Fallback to basic digit hunt if structure doesn't match perfectly
        val fallbackRegex = Pattern.compile("(?i)(?:XX|X|No|ending)\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b")
        val matcher = fallbackRegex.matcher(body)
        return if (matcher.find()) matcher.group(1) else null
    }
}
