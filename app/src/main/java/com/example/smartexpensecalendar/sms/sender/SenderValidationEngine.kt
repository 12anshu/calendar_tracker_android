package com.example.smartexpensecalendar.sms.sender

import com.example.smartexpensecalendar.domain.model.SenderType
import com.example.smartexpensecalendar.sms.sender.SenderValidationResult
import com.example.smartexpensecalendar.sms.config.SenderRegistry

object SenderValidationEngine {

    private val bankSenders = SenderRegistry.bankSenders

    private val cardSenders = SenderRegistry.cardSenders

    private val upiSenders = SenderRegistry.upiSenders

    fun validate(
        sender: String?
    ): SenderValidationResult {

        if (sender.isNullOrBlank()) {
            return SenderValidationResult(
                SenderType.UNKNOWN
            )
        }

        val normalized =
            normalize(sender)

        return when {

            cardSenders.any {
                normalized.contains(it)
            } ->
                SenderValidationResult(
                    SenderType.CARD
                )

            bankSenders.any {
                normalized.contains(it)
            } ->
                SenderValidationResult(
                    SenderType.BANK
                )

            upiSenders.any {
                normalized.contains(it)
            } ->
                SenderValidationResult(
                    SenderType.UPI
                )

            else ->
                SenderValidationResult(
                    SenderType.UNKNOWN
                )
        }
    }

    private fun normalize(
        sender: String
    ): String {

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