package com.example.smartexpensecalendar.new_sms_engine.common.tokenizer

import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BalanceSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BankingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BillingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CurrencySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.FinancialSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.InformationSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ObligationSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.TransactionNegativeSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.AuxiliarySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.PrepositionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.TemporalSignals

/**
 * Converts raw words into semantic tokens.
 *
 * This is the ONLY class in the engine that understands
 * keywords. Every downstream component works only with
 * TokenCategory.
 */
object TokenClassifier {

    /**
     * Maps a token category to its corresponding signal set.
     */
    private val CATEGORY_MAP: Map<TokenCategory, Set<String>> = mapOf(
        TokenCategory.AUXILIARY_PRESENT to AuxiliarySignals.PRESENT,
        TokenCategory.AUXILIARY_PAST to AuxiliarySignals.PAST,
        TokenCategory.AUXILIARY_FUTURE to AuxiliarySignals.FUTURE,
        TokenCategory.AUXILIARY_PARTICIPLE to AuxiliarySignals.PARTICIPLES,
        TokenCategory.TEMPORAL_RELATIVE to TemporalSignals.RELATIVE,
        TokenCategory.TEMPORAL_SCHEDULING to TemporalSignals.SCHEDULING,
        TokenCategory.PREPOSITION_SOURCE to PrepositionSignals.SOURCE,
        TokenCategory.PREPOSITION_DESTINATION to PrepositionSignals.DESTINATION,
        TokenCategory.PREPOSITION_LOCATION to PrepositionSignals.LOCATION,
        TokenCategory.PREPOSITION_MEDIUM to PrepositionSignals.MEDIUM,
        TokenCategory.PREPOSITION_PURPOSE to PrepositionSignals.PURPOSE,
        TokenCategory.PREPOSITION_REFERENCE to PrepositionSignals.REFERENCE,
        TokenCategory.DEBIT_ACTION to ActionSignals.DEBIT_ACTION_SIGNALS,
        TokenCategory.CREDIT_ACTION to ActionSignals.CREDIT_ACTION_SIGNALS,
        TokenCategory.REFUND_ACTION to ActionSignals.REFUND_ACTION_SIGNALS,
        TokenCategory.REWARD_ACTION to ActionSignals.REWARD_ACTION_SIGNALS,
        TokenCategory.TRANSACTION_ACTION to ActionSignals.TRANSACTION_ACTION_SIGNALS,
        TokenCategory.SUCCESS_STATUS to StatusSignals.SUCCESS_SIGNALS,
        TokenCategory.FAILURE_STATUS to StatusSignals.FAILURE_SIGNALS,
        TokenCategory.PENDING_STATUS to StatusSignals.PENDING_SIGNALS,
        TokenCategory.PAYMENT_MODE to PaymentSignals.ALL,
        TokenCategory.BALANCE to BalanceSignals.ALL,
        TokenCategory.BILLING_OBJECT to FinancialSignals.DUE_CONTEXT,
        TokenCategory.AMOUNT_DESCRIPTOR to BillingSignals.AMOUNT_DESCRIPTORS,
        TokenCategory.CURRENCY to CurrencySignals.CURRENCY_INDICATORS,
        TokenCategory.LIABILITY_STATE to ObligationSignals.LIABILITY_STATE_SIGNALS,
        TokenCategory.LIABILITY_OBJECT to ObligationSignals.LIABILITY_OBJECT_SIGNALS,
        TokenCategory.REQUEST_ACTION to ObligationSignals.REQUEST_ACTION_SIGNALS,
        TokenCategory.AUTHORIZATION to TransactionNegativeSignals.AUTHORIZATION_SIGNALS,
        TokenCategory.AUTHENTICATION to TransactionNegativeSignals.AUTHENTICATION_SIGNALS,
        TokenCategory.FAILURE to TransactionNegativeSignals.FAILURE_SIGNALS,
        TokenCategory.STATEMENT to InformationSignals.STATEMENT_SIGNALS,
        TokenCategory.ACCOUNT_UPDATE to InformationSignals.ACCOUNT_UPDATE_SIGNALS,
        TokenCategory.SECURITY to InformationSignals.SECURITY_SIGNALS,
        TokenCategory.REWARD to InformationSignals.REWARD_SIGNALS
    )

    private val CLEAN_REGEX = Regex("[^A-Z0-9]")

    /**
     * Inverted map for O(1) semantic lookup.
     */
    private val INVERTED_CATEGORY_MAP: Map<String, Set<TokenCategory>> by lazy {
        val result = mutableMapOf<String, MutableSet<TokenCategory>>()
        
        CATEGORY_MAP.forEach { (category, signals) ->
            signals.forEach { signal ->
                val normalized = signal.uppercase().replace(CLEAN_REGEX, "")
                if (normalized.isNotEmpty()) {
                    result.getOrPut(normalized) { mutableSetOf() }.add(category)
                }
            }
        }
        
        result
    }

    /**
     * Classify all words.
     */
    fun classify(
        words: List<Pair<String, Int>>
    ): List<Token> {
        return words.mapIndexed { index, (word, lineIndex) ->

            val normalized = word
                .uppercase()
                .replace(CLEAN_REGEX, "")

            Token(
                text = word,
                index = index,
                lineIndex = lineIndex,
                categories = categoriesFor(normalized)
            )
        }
    }

    /**
     * Returns all semantic categories applicable to a word.
     */
    private fun categoriesFor(
        normalizedWord: String
    ): Set<TokenCategory> {

        val categories = INVERTED_CATEGORY_MAP[normalizedWord]

        if (categories == null || categories.isEmpty()) {
            return setOf(TokenCategory.UNKNOWN)
        }

        return categories
    }
}
