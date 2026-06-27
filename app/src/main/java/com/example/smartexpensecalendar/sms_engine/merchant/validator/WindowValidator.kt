package com.example.smartexpensecalendar.sms_engine.merchant.validator

object WindowValidator {

    fun isValidStandaloneWindow(
        text: String
    ): Boolean {

        val cleaned = text.trim()

        if (cleaned.length < 3)
            return false

        if (cleaned.length > 60)
            return false

        return true
    }
}