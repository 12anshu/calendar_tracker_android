package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object ObligationPatterns {

    /**
     * EMI Due
     * Bill Due
     * Premium Due
     * Payment Due
     */
    val LIABILITY_OBJECT_STATE = Pattern(
        name = "LIABILITY_OBJECT_STATE",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(TokenCategory.LIABILITY_OBJECT),
            CategoryToken(TokenCategory.LIABILITY_STATE)
        )
    )

    /**
     * Outstanding Balance
     * Pending Payment
     * Overdue EMI
     * Payable Charges
     */
    val LIABILITY_STATE_OBJECT = Pattern(
        name = "LIABILITY_STATE_OBJECT",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(TokenCategory.LIABILITY_STATE),
            CategoryToken(TokenCategory.LIABILITY_OBJECT)
        )
    )

    /**
     * Pay Bill
     * Pay EMI
     * Renew Premium
     * Clear Dues
     */
    val REQUEST_ACTION_OBJECT = Pattern(
        name = "REQUEST_ACTION_OBJECT",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(TokenCategory.REQUEST_ACTION),
            CategoryToken(TokenCategory.LIABILITY_OBJECT)
        )
    )

    val ALL = listOf(
        LIABILITY_OBJECT_STATE,
        LIABILITY_STATE_OBJECT,
        REQUEST_ACTION_OBJECT
    )
}