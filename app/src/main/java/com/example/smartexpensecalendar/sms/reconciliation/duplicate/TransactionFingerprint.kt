package com.example.smartexpensecalendar.sms.reconciliation.duplicate

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.PaymentMethod

data class TransactionFingerprint(
    val amount: Double,
    val direction: TransactionDirection,
    val merchant: String?,
    val paymentMethod: PaymentMethod,
    val senderFamily: String?
)
