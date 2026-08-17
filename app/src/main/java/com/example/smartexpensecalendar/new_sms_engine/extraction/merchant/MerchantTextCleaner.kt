package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant

import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAccount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAction
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAmount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsBalance
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsCurrency
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsDate
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsFailure
import com.example.smartexpensecalendar.new_sms_engine.common.regex.UpiRegex.UPI_PREFIX_REGEX
import com.example.smartexpensecalendar.new_sms_engine.common.regex.EdgePunctuationRegex.EDGE_PUNCTUATION_REGEX
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantConstants.MAX_MERCHANT_TOKENS

object MerchantTextCleaner {
    fun clean(
        text: String
    ): String {

        return text
            .trim()
            .split(Regex("""\s+"""))
            .map(::sanitizeToken)
            .filter(::isValidMerchantToken)
            .take(MAX_MERCHANT_TOKENS)
            .joinToString(" ")
    }

    /**
     * Performs only lexical cleanup.
     *
     * Does NOT perform merchant normalization.
     */
    private fun sanitizeToken(
        token: String
    ): String {

        return token
            .replace(UPI_PREFIX_REGEX, "")
            .replace(EDGE_PUNCTUATION_REGEX, "")
            .trim()
    }

    /**
     * Returns true if the token can be part of a merchant name.
     */
    private fun isValidMerchantToken(
        token: String
    ): Boolean {

        if (token.isBlank()) {
            return false
        }

        if (token.startsWith("http", ignoreCase = true) ||
            token.startsWith("www.", ignoreCase = true)
        ) {
            return false
        }

        if (containsAmount(token)) {
            return false
        }

        if (containsDate(token)) {
            return false
        }

        if (containsCurrency(token)) {
            return false
        }

        if (containsAccount(token)) {
            return false
        }

        if (containsBalance(token)) {
            return false
        }

        if (containsFailure(token)) {
            return false
        }

        if (containsAction(token)) {
            return false
        }

        return true
    }
}