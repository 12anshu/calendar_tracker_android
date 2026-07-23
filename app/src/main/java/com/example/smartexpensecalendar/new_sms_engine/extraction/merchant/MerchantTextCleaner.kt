package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant

import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAccount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAction
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAmount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsBalance
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsCurrency
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsDate
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsFailure
import com.example.smartexpensecalendar.new_sms_engine.common.regex.UpiPrefixRegex.UPI_PREFIX_REGEX
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantConstants.MAX_MERCHANT_TOKENS

object MerchantTextCleaner {

    fun clean(
        text: String
    ): String {

        val merchant = text
            .trim()
            .split(Regex("""\s+"""))
            .map(::normalizeToken)
            .filter(::isMerchantCandidate)
            .take(MAX_MERCHANT_TOKENS)
            .joinToString(" ")
        return merchant
    }

    private fun normalizeToken(
        token: String
    ): String {

        return token
            .replace(UPI_PREFIX_REGEX, "")
            .trim()
    }

    fun isMerchantCandidate(
        token: String
    ): Boolean {

        val text = token.trim()

        if (text.isBlank()) {
            return false
        }

        if (text.startsWith("http", ignoreCase = true) ||
            text.startsWith("www.", ignoreCase = true)
        ) {
            return false
        }

        if (containsAmount(text)) {
            return false
        }

        if (containsDate(text)) {
            return false
        }

        if (containsCurrency(text)) {
            return false
        }

        if (containsAccount(text)) {
            return false
        }

        if (containsBalance(text)) {
            return false
        }

        if (containsFailure(text)) {
            return false
        }

        if (containsAction(text)) {
            return false
        }

        return true
    }
}