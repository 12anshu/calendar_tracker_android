package com.example.smartexpensecalendar.domain.model

data class SMSProcessingLog(
    val smsId: Long,
    val sender: String,
    val body: String,
    val date: Long,
    val status: ProcessingStatus,
    val failureReason: String? = null,
    val parsedAmount: Double? = null,
    val parsedMerchant: String? = null
)

enum class ProcessingStatus {
    PROCESSED,
    SKIPPED,
    FAILED,
    IGNORED,
    SYNC_COMPLETE
}
