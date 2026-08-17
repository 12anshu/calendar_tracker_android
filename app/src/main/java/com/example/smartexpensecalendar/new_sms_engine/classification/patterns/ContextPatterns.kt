package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.AnyWordToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.OptionalToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object ContextPatterns {


    /**
     * Due by
     * Pay before
     * Due on
     */
    val SCHEDULING = Pattern(
        name = "SCHEDULING",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        tokens = listOf(

            CategoryToken(
                TokenCategory.TEMPORAL_SCHEDULING
            ),

            AnyWordToken
        )
    )

    /**
     * Completed EMI conversion.
     *
     * Examples:
     * - transaction converted to EMI
     * - transaction converted into EMI
     * - transaction has been converted into EMI
     * - purchase converted to EMI
     *
     * The conversion verb itself establishes the completed lifecycle
     * state. The connective ("to"/"into") is intentionally not required.
     */
    val EMI_CONVERSION_COMPLETED = Pattern(
        name = "EMI_CONVERSION_COMPLETED",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 6,
        tokens = listOf(
            CategoryToken(TokenCategory.EMI_CONVERSION_COMPLETED),
            CategoryToken(TokenCategory.LIABILITY_OBJECT)
        )
    )

    /**
     * Explicit MAB / AMB status with an amount.
     *
     * Examples:
     * - MAB INR 9536.49
     * - AMB Rs. 4500
     */
    val MAB_AMOUNT = Pattern(
        name = "MAB_AMOUNT",
        evidenceType = EvidenceType.MAB_ADVISORY_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.MAB_REQUIREMENT),
            CategoryToken(TokenCategory.CURRENCY)
        )
    )

    /**
     * Account balance explicitly tied to an MAB requirement.
     *
     * Example:
     * "Your DBS A/c bal is now below INR 10000...
     *  For details on Monthly Avg Bal (MAB) requirements..."
     */
    val ACCOUNT_BALANCE_MAB = Pattern(
        name = "ACCOUNT_BALANCE_MAB",
        evidenceType = EvidenceType.MAB_ADVISORY_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 10,
        tokens = listOf(
            CategoryToken(TokenCategory.ACCOUNT_BALANCE),
            CategoryToken(TokenCategory.MAB_REQUIREMENT)
        )
    )

    /**
     * Pre-disbursement workflow.
     *
     * Examples:
     * - loan amount ready for disbursal
     * - disbursement pending
     * - disbursement initiated
     * - disbursement processing
     * - disbursement scheduled
     * - approved for disbursal
     *
     * This does NOT mean money has moved.
     */
    val PRE_DISBURSAL_WORKFLOW = Pattern(
        name = "PRE_DISBURSAL_WORKFLOW",
        evidenceType = EvidenceType.PRE_DISBURSAL_WORKFLOW,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 6,
        tokens = listOf(
            CategoryToken(TokenCategory.DISBURSEMENT_TERM),
            CategoryToken(TokenCategory.DISBURSEMENT_STATE)
        )
    )

    /**
     * Disbursement is ready but has not been completed.
     *
     * Examples:
     * - "Funds ready for Disbursal"
     * - "Amount is ready for disbursal"
     *
     * READY is a workflow state. It does not indicate that
     * the money has actually moved.
     */
    val DISBURSEMENT_READY_WORKFLOW = Pattern(
        name = "DISBURSEMENT_READY_WORKFLOW",
        evidenceType = EvidenceType.PRE_DISBURSAL_WORKFLOW,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.DISBURSEMENT_STATE),
            CategoryToken(TokenCategory.DISBURSEMENT_TERM)
        )
    )

    /**
     * Future disbursement.
     *
     * Examples:
     * - amount will be disbursed
     * - loan will be disbursed
     * - amount shall be disbursed
     *
     * This is not a completed movement.
     */
    val FUTURE_DISBURSEMENT = Pattern(
        name = "FUTURE_DISBURSEMENT",
        evidenceType = EvidenceType.PRE_DISBURSAL_WORKFLOW,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 2,
        tokens = listOf(
            CategoryToken(TokenCategory.AUXILIARY_FUTURE),
            CategoryToken(TokenCategory.AUXILIARY_PARTICIPLE),
            CategoryToken(TokenCategory.DISBURSEMENT_TERM)
        )
    )

    /**
     * Disbursement process will occur later.
     *
     * Examples:
     * - disbursement will be processed
     * - disbursement will be initiated
     * - disbursal will happen shortly
     */
    val DISBURSEMENT_FUTURE_WORKFLOW = Pattern(
        name = "DISBURSEMENT_FUTURE_WORKFLOW",
        evidenceType = EvidenceType.PRE_DISBURSAL_WORKFLOW,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 4,
        tokens = listOf(
            CategoryToken(TokenCategory.DISBURSEMENT_TERM),
            CategoryToken(TokenCategory.AUXILIARY_FUTURE)
        )
    )

    /**
     * Statement delivery / notification.
     *
     * Examples:
     * - statement is sent to your email
     * - statement was sent to your registered email
     * - statement has been sent to your email
     * - statement sent to your registered email
     *
     * "sent" here represents delivery of the statement/document,
     * not movement of money.
     */
    val STATEMENT_DELIVERY = Pattern(
        name = "STATEMENT_DELIVERY",
        evidenceType = EvidenceType.INFORMATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        maxGap = 6,
        tokens = listOf(
            CategoryToken(TokenCategory.STATEMENT),
            CategoryToken(TokenCategory.DEBIT_ACTION),
            CategoryToken(TokenCategory.PREPOSITION_DESTINATION),
            AnyWordToken
        )
    )

    val ALL = listOf(
        SCHEDULING,
        EMI_CONVERSION_COMPLETED,
        MAB_AMOUNT,
        ACCOUNT_BALANCE_MAB,
        PRE_DISBURSAL_WORKFLOW,
        DISBURSEMENT_READY_WORKFLOW,
        FUTURE_DISBURSEMENT,
        DISBURSEMENT_FUTURE_WORKFLOW,
        STATEMENT_DELIVERY
    )
}