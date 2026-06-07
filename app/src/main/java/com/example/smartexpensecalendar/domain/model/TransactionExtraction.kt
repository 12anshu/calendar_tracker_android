package com.example.smartexpensecalendar.domain.model

data class TransactionExtraction(

    val amount: Double?,

    val merchant: String?,

    val mode: TransactionMode,

    val direction: TransactionDirection,

    val eventType: FinancialEventType,

    val confidence: Int
)
