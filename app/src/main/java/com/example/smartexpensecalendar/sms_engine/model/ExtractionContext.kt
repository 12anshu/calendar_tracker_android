package com.example.smartexpensecalendar.sms_engine.model

import com.example.smartexpensecalendar.sms_engine.direction.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.event.FinancialEventType
import com.example.smartexpensecalendar.sms_engine.message.MessageType

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
    val direction: TransactionDirection,

    /**
     * CREDIT_CARD_SPEND
     * UPI_PAYMENT
     * REFUND
     * etc.
     */
    val eventType: FinancialEventType?
)