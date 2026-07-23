package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Actions
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object ActionPatterns {

    private val FINANCIAL_MOVEMENT_ACTIONS = setOf(
        TokenCategory.DEBIT_ACTION,
        TokenCategory.CREDIT_ACTION,
        TokenCategory.TRANSACTION_ACTION,
        TokenCategory.REFUND_ACTION
    )

    val ACTION_AMOUNT = Pattern(
        name = Actions.ACTION_AMOUNT,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.CURRENCY)
        )
    )

    val AMOUNT_ACTION = Pattern(
        name = Actions.AMOUNT_ACTION,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(TokenCategory.CURRENCY),
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS)
        )
    )

    val ACTION_FROM = Pattern(
        name = Actions.ACTION_FROM,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_SOURCE)
        )
    )

    val ACTION_TO = Pattern(
        name = Actions.ACTION_TO,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_DESTINATION)
        )
    )

    val ACTION_AT = Pattern(
        name = Actions.ACTION_AT,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_LOCATION)
        )
    )

    val ACTION_VIA = Pattern(
        name = Actions.ACTION_VIA,
        evidenceType = EvidenceType.COMPLETED_ACTION,
        strength = EvidenceStrength.HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(FINANCIAL_MOVEMENT_ACTIONS),
            CategoryToken(TokenCategory.PREPOSITION_MEDIUM)
        )
    )

    val ALL = listOf(
        ACTION_AMOUNT,
        AMOUNT_ACTION,
        ACTION_AT,
        ACTION_TO,
        ACTION_VIA,
        ACTION_FROM
    )

}