package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object MandatePatterns {

    /**
     * Mandate successfully created
     * Mandate is successfully created
     * UPI mandate created
     *
     * Represents mandate creation / authorization lifecycle,
     * not an actual money movement.
     */
    val MANDATE_CREATED = Pattern(
        name = "MANDATE_CREATED",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.MANDATE),
            CategoryToken(TokenCategory.PROCESS_STATE)
        )
    )

    /**
     * Mandate was successfully executed
     * Mandate successfully executed
     *
     * Represents the mandate execution lifecycle acknowledgement.
     * The actual account debit notification remains TRANSACTION.
     */
    val MANDATE_EXECUTED = Pattern(
        name = "MANDATE_EXECUTED",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.MANDATE),
            CategoryToken(TokenCategory.SUCCESS_STATUS)
        )
    )

    /**
     * Mandate has been cancelled
     * Mandate is revoked
     *
     * Represents the cancellation or revocation of a mandate.
     */
    val MANDATE_CANCELLED = Pattern(
        name = "MANDATE_CANCELLED",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.MANDATE),
            CategoryToken(TokenCategory.FAILURE_STATUS)
        )
    )

    val AUTOPAY_ACTIVATION_CONFIRMATION = Pattern(
        name = "AUTOPAY_ACTIVATION_CONFIRMATION",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 1,
        tokens = listOf(
            CategoryToken(TokenCategory.AUTOPAY),
            CategoryToken(TokenCategory.SUCCESS_STATUS),
            CategoryToken(TokenCategory.TRANSACTION_ACTION),
            CategoryToken(TokenCategory.CURRENCY)
        )
    )

    val ALL = listOf(
        MANDATE_CREATED,
        MANDATE_EXECUTED,
        MANDATE_CANCELLED,
        AUTOPAY_ACTIVATION_CONFIRMATION
    )
}