package com.example.smartexpensecalendar.sms.detection

data class DetectionResult(

    val isFinancial: Boolean,

    val transactionClass: TransactionClass,

    val confidence: Int,

    val detectedFields: Set<DetectedField>
)