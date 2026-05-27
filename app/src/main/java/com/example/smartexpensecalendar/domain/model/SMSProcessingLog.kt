package com.example.smartexpensecalendar.domain.model

data class SMSProcessingLog(
    val smsId: Long,
    val sender: String,
    val body: String,
    val date: Long,
    val status: ProcessingStatus,
    val failureReason: String? = null
)

enum class ProcessingStatus {
    PROCESSED,
    SKIPPED,
    FAILED
}
