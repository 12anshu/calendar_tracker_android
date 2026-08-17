package com.example.smartexpensecalendar.new_sms_engine.common.tokenizer

import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.AutoPaySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BalanceSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BillingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CurrencySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.DisbursementSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.FinancialSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.InformationSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.LifecycleSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.MABSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.MandateSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ObligationSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.RevisionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.TransactionNegativeSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.AuxiliarySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.ConditionalSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.NegationSignals
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


        TokenCategory.NEGATION to NegationSignals.NEGATION,
        TokenCategory.CONDITIONAL to ConditionalSignals.CONDITIONAL,

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

        TokenCategory.EMI_CONVERSION_REQUEST
                to LifecycleSignals.EMI_CONVERSION_REQUEST_SIGNALS,
        TokenCategory.EMI_CONVERSION_COMPLETED
                to LifecycleSignals.EMI_CONVERSION_COMPLETED_SIGNALS,
        TokenCategory.EMI_CONVERSION_NOUN
                to LifecycleSignals.EMI_CONVERSION_NOUN_SIGNALS,

        TokenCategory.DISBURSEMENT_TERM
                to DisbursementSignals.TERM_SIGNALS,

        TokenCategory.DISBURSEMENT_STATE
                to DisbursementSignals.STATE_SIGNALS,

        TokenCategory.TRANSACTION_ACTION to ActionSignals.TRANSACTION_ACTION_SIGNALS,
        TokenCategory.SUCCESS_STATUS to StatusSignals.SUCCESS_SIGNALS,
        TokenCategory.FAILURE_STATUS to StatusSignals.FAILURE_SIGNALS,
        TokenCategory.PENDING_STATUS to StatusSignals.PENDING_SIGNALS,
        TokenCategory.PAYMENT_MODE to PaymentSignals.ALL,
        TokenCategory.MANDATE to MandateSignals.MANDATE_INDICATORS,
        TokenCategory.AUTOPAY to AutoPaySignals.AUTOPAY_INDICATORS,
        TokenCategory.BALANCE to BalanceSignals.ALL,
        TokenCategory.ACCOUNT_BALANCE to BalanceSignals.BALANCE_INDICATORS,
        TokenCategory.MAB_REQUIREMENT to MABSignals.ALL,
        TokenCategory.BILLING_OBJECT to FinancialSignals.DUE_CONTEXT,
        TokenCategory.AMOUNT_DESCRIPTOR to BillingSignals.AMOUNT_DESCRIPTORS,
        TokenCategory.CURRENCY to CurrencySignals.CURRENCY_INDICATORS,
        TokenCategory.LIABILITY_STATE to ObligationSignals.LIABILITY_STATE_SIGNALS,
        TokenCategory.LIABILITY_OBJECT to ObligationSignals.LIABILITY_OBJECT_SIGNALS,
        TokenCategory.REQUEST_ACTION to ObligationSignals.REQUEST_ACTION_SIGNALS,
        TokenCategory.REVISION_ACTION to RevisionSignals.REVISION_SIGNALS,
        TokenCategory.AUTHORIZATION to TransactionNegativeSignals.AUTHORIZATION_SIGNALS,
        TokenCategory.AUTHENTICATION to TransactionNegativeSignals.AUTHENTICATION_SIGNALS,
        TokenCategory.FAILURE to TransactionNegativeSignals.FAILURE_SIGNALS,
        TokenCategory.STATEMENT to InformationSignals.STATEMENT_SIGNALS,
        TokenCategory.ACCOUNT_UPDATE to InformationSignals.ACCOUNT_UPDATE_SIGNALS,
        TokenCategory.SECURITY to InformationSignals.SECURITY_SIGNALS,
        TokenCategory.REWARD to InformationSignals.REWARD_SIGNALS,
        TokenCategory.RETIREMENT to InformationSignals.RETIREMENT_SIGNALS,
        TokenCategory.INVESTMENT to InformationSignals.INVESTMENT_SIGNALS,
        TokenCategory.TAX to InformationSignals.TAX_SIGNALS,
        TokenCategory.DOCUMENT to InformationSignals.DOCUMENT_SIGNALS,
        TokenCategory.LOAN_LIFECYCLE to InformationSignals.LOAN_LIFECYCLE_SIGNALS,
        TokenCategory.PROCESS_STATE to InformationSignals.PROCESS_STATE_SIGNALS
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
                categories = categoriesFor(word)
            )
        }
    }

    /**
     * Returns all semantic categories applicable to a word.
     */
    private fun categoriesFor(
        word: String
    ): Set<TokenCategory> {

        val alternatives = word
            .uppercase()
            .split("/")

        val categories = alternatives
            .map { alternative ->
                alternative.replace(CLEAN_REGEX, "")
            }
            .filter { it.isNotEmpty() }
            .flatMap { normalized ->
                INVERTED_CATEGORY_MAP[normalized].orEmpty()
            }
            .toSet()

        if (categories.isEmpty()) {
            return setOf(TokenCategory.UNKNOWN)
        }

        return categories
    }
}
