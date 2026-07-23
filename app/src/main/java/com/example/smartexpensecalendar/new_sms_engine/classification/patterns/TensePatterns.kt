package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.OptionalToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.language.AuxiliarySignals
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

/**
 * Language tense patterns.
 *
 * These patterns identify whether a financial action
 * has already happened, is happening or will happen.
 */
object TensePatterns {

    private val FINANCIAL_MOVEMENT_ACTIONS = setOf(
        TokenCategory.DEBIT_ACTION,
        TokenCategory.CREDIT_ACTION,
        TokenCategory.TRANSACTION_ACTION,
        TokenCategory.REFUND_ACTION
    )

    /**
     * HAS BEEN DEBITED
     * HAVE BEEN CREDITED
     * HAD BEEN TRANSFERRED
     */

    val COMPLETED_PERFECT = Pattern(
        name = "COMPLETED_PERFECT",
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.VERY_HIGH,
        tokens = listOf(
            CategoryToken(TokenCategory.AUXILIARY_PRESENT),
            CategoryToken(TokenCategory.AUXILIARY_PARTICIPLE),
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS)
        )
    )

    /**
     * WAS DEBITED
     * WERE CREDITED
     */
    val COMPLETED_PAST = Pattern(
        name = "COMPLETED_PAST",
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(
            CategoryToken(TokenCategory.AUXILIARY_PAST),
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS)
        )
    )

    /**
     * IS DEBITED
     * HAS DEBITED
     */
    val COMPLETED_PRESENT = Pattern(
        name = "COMPLETED_PRESENT",
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(
            CategoryToken(TokenCategory.AUXILIARY_PRESENT),
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS)
        )
    )

    /**
     * WILL BE DEBITED
     * SHALL BE CREDITED
     */
    val FUTURE = Pattern(
        name = "FUTURE",
        evidenceType = EvidenceType.FUTURE_ACTION,
        strength = EvidenceStrength.VERY_HIGH,
        tokens = listOf(
            CategoryToken(TokenCategory.AUXILIARY_FUTURE),
            CategoryToken(TokenCategory.AUXILIARY_PARTICIPLE),
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS)
        )
    )

    /**
     * All tense patterns.
     */
    val ALL = listOf(
        COMPLETED_PERFECT,
        COMPLETED_PAST,
        COMPLETED_PRESENT,
        FUTURE
    )
}