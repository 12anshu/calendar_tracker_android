package com.example.smartexpensecalendar.sms_engine.detector

data class DetectionResult(

    val isFinancial: Boolean,

    val transactionClass: TransactionClass,

    val confidence: Int,

    val detectedFields: Set<DetectedField>
)
