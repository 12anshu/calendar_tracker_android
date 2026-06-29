package com.example.smartexpensecalendar.new_sms_engine.entity.context

import com.example.smartexpensecalendar.new_sms_engine.classification.models.DirectionResult
import com.example.smartexpensecalendar.new_sms_engine.classification.models.FinancialEventResult
import com.example.smartexpensecalendar.new_sms_engine.classification.models.MessageTypeResult
import com.example.smartexpensecalendar.new_sms_engine.classification.models.TransactionModeResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Shared context produced by the Classification layer.
 */
data class ExtractionContext(

    /**
     * Qualification context.
     */
    val qualificationContext: QualificationContext,

    /**
     * Financial classification.
     */
//    val financial: FinancialResult,

    /**
     * Message type classification.
     */
    val messageType: MessageTypeResult,

    /**
     * Transaction direction.
     */
    val direction: DirectionResult,

    /**
     * Financial event.
     */
    val financialEvent: FinancialEventResult,

    /**
     * Transaction mode.
     */
    val transactionMode: TransactionModeResult,

    /**
     * Extracted amount.
     */
    val amount: Double? = null,

    /**
     * Currency.
     */
    val currency: String = "INR"
)