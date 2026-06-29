package com.example.smartexpensecalendar.sms_engine.model

import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.MessageType


data class ExtractionContext(

    /**
     * Message already verified as financial.
     */
    val isFinancial: Boolean,

    /**
     * TRANSACTION / OBLIGATION / INFORMATION
     */
    val messageType: MessageType,

    /**
     * DEBIT / CREDIT / UNKNOWN
     */
//    val direction: TransactionDirection,

    /**
     * CREDIT_CARD_SPEND
     * UPI_PAYMENT
     * REFUND
     * etc.
     */
    val eventType: FinancialEventType?
)