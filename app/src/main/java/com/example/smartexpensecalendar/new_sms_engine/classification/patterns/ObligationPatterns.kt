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

    /**
     * Payment Due → Amount Descriptor
     *
     * Example:
     * "due minimum amount due..."
     */
    val BILLING_OBJECT_AMOUNT_DESCRIPTOR = Pattern(
        name = "BILLING_OBJECT_AMOUNT_DESCRIPTOR",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 0,
        tokens = listOf(
            CategoryToken(TokenCategory.BILLING_OBJECT),
            CategoryToken(TokenCategory.AMOUNT_DESCRIPTOR)
        )
    )

    /**
     * Payment Due → Another Billing Object
     *
     * Example:
     * "amount is due Due..."
     */
    val BILLING_OBJECT_BILLING_OBJECT = Pattern(
        name = "BILLING_OBJECT_BILLING_OBJECT",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 0,
        tokens = listOf(
            CategoryToken(TokenCategory.BILLING_OBJECT),
            CategoryToken(TokenCategory.BILLING_OBJECT)
        )
    )

    /**
     * Overdue → Financial Instrument
     *
     * Example:
     * "amount is overdue on your Bank Credit Card"
     */
    val BILLING_OBJECT_PAYMENT_MODE = Pattern(
        name = "BILLING_OBJECT_PAYMENT_MODE",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 0,
        tokens = listOf(
            CategoryToken(TokenCategory.BILLING_OBJECT),
            CategoryToken(TokenCategory.PAYMENT_MODE)
        )
    )

    val AMOUNT_LIABILITY_STATE_PAYMENT_MODE = Pattern(
        name = "AMOUNT_LIABILITY_STATE_PAYMENT_MODE",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 6,
        tokens = listOf(
            CategoryToken(TokenCategory.CURRENCY),
            CategoryToken(TokenCategory.LIABILITY_STATE),
            CategoryToken(TokenCategory.PAYMENT_MODE)
        )
    )

    /**
     * Payment Due → Relative Time
     *
     * Example:
     * "payment is due today"
     */
    val BILLING_OBJECT_TEMPORAL_RELATIVE = Pattern(
        name = "BILLING_OBJECT_TEMPORAL_RELATIVE",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 0,
        tokens = listOf(
            CategoryToken(TokenCategory.BILLING_OBJECT),
            CategoryToken(TokenCategory.TEMPORAL_RELATIVE)
        )
    )

    val ALL = listOf(
        LIABILITY_OBJECT_STATE,
        LIABILITY_STATE_OBJECT,
        REQUEST_ACTION_OBJECT,
        AMOUNT_LIABILITY_STATE_PAYMENT_MODE,
        BILLING_OBJECT_TEMPORAL_RELATIVE,
        BILLING_OBJECT_AMOUNT_DESCRIPTOR,
        BILLING_OBJECT_BILLING_OBJECT,
        BILLING_OBJECT_PAYMENT_MODE
    )
}