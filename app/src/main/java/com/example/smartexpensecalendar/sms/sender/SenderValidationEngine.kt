package com.example.smartexpensecalendar.sms.sender

import com.example.smartexpensecalendar.domain.model.SenderType
import com.example.smartexpensecalendar.sms.config.SenderRegistry
import java.util.regex.Pattern

object SenderValidationEngine {

    private val bankSenders = SenderRegistry.bankSenders
    private val cardSenders = SenderRegistry.cardSenders
    private val upiSenders = SenderRegistry.upiSenders

    private val familyPattern = Pattern.compile("(?<=-|^)([A-Z]{4,8})(?=-|$)")

    fun validate(sender: String?): SenderValidationResult {
        if (sender.isNullOrBlank()) {
            return SenderValidationResult(SenderType.UNKNOWN)
        }

        val normalized = normalize(sender)

        return when {
            cardSenders.any { normalized.contains(it) } ->
                SenderValidationResult(SenderType.CARD)

            bankSenders.any { normalized.contains(it) } ->
                SenderValidationResult(SenderType.BANK)

            upiSenders.any { normalized.contains(it) } ->
                SenderValidationResult(SenderType.UPI)

            else ->
                SenderValidationResult(SenderType.UNKNOWN)
        }
    }

    /**
     * Resolves a consistent "Family" name for a sender header.
     * e.g., JM-DBSBNK-S -> "DBS Bank"
     * e.g., AD-HDFCBK -> "HDFC Bank"
     */
    fun resolveFamily(sender: String?): String? {
        if (sender.isNullOrBlank()) return null
        val upperSender = sender.uppercase()

        // 1. Try to extract TRAI code and match against registry
        val matcher = familyPattern.matcher(upperSender)
        while (matcher.find()) {
            val code = matcher.group(1) ?: continue
            SenderRegistry.bankCodeMap[code]?.let { return it }
        }

        // 2. Fallback: check if raw sender contains any known bank names (for global/numeric senders)
        // We reuse the list of friendly names from the registry for matching
        return SenderRegistry.bankCodeMap.values.find { upperSender.contains(it.uppercase()) }
    }

    private fun normalize(sender: String): String {
        return sender
            .uppercase()
            .replace("AD-", "")
            .replace("AX-", "")
            .replace("VM-", "")
            .replace("VK-", "")
            .replace("BZ-", "")
            .replace("JD-", "")
            .trim()
    }
}
