package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.AnyWordToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object StructuralPatterns {

    private val FINANCIAL_MOVEMENT_ACTIONS = setOf(
        TokenCategory.DEBIT_ACTION,
        TokenCategory.CREDIT_ACTION,
        TokenCategory.TRANSACTION_ACTION,
        TokenCategory.REFUND_ACTION
    )

    /**
     * Paid to Amazon
     * Sent to Rahul
     */
    val ACTION_TO_ENTITY = Pattern(
        name = "ACTION_TO_ENTITY",
        evidenceType = EvidenceType.DESTINATION_ENTITY,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(

            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_DESTINATION),
            AnyWordToken
        )
    )

    /**
     * Received from Rahul
     * Debited from Account
     */
    val ACTION_FROM_ENTITY = Pattern(
        name = "ACTION_FROM_ENTITY",
        evidenceType = EvidenceType.SOURCE_ENTITY,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(

            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_SOURCE),
            AnyWordToken
        )
    )

    /**
     * Paid via UPI
     * Sent using IMPS
     */
    val ACTION_VIA_MODE = Pattern(
        name = "ACTION_VIA_MODE",
        evidenceType = EvidenceType.PAYMENT_MODE_CONTEXT,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(

            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_MEDIUM),
            CategoryToken(TokenCategory.PAYMENT_MODE)
        )
    )

    /**
     * Paid at DMart
     * Purchased at Amazon
     */
    val ACTION_AT_LOCATION = Pattern(
        name = "ACTION_AT_LOCATION",
        evidenceType = EvidenceType.MERCHANT_CONTEXT,
        strength = EvidenceStrength.HIGH,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_LOCATION),
            AnyWordToken
        )
    )

    val ALL = listOf(

        ACTION_TO_ENTITY,

        ACTION_FROM_ENTITY,

        ACTION_VIA_MODE,

        ACTION_AT_LOCATION
    )
}