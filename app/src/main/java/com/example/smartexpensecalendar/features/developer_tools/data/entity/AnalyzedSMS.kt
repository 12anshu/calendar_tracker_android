package com.example.smartexpensecalendar.features.developer_tools.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyzed_sms")
data class AnalyzedSMS(
    @PrimaryKey val id: Long,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val normalizedMessage: String,
    val senderType: String,
    val isFinancial: Boolean,
    val score: Int,
    val confidence: Int,
    val matchedSignals: Set<String>,
    val matchedKeywords: Set<String> = emptySet(),
    val matchedPatterns: Set<String> = emptySet(),
    val negativeSignals: Set<String> = emptySet(),
    val scoreBreakdown: Map<String, Int> = emptyMap(),
    val template: String = "",
    val messageType: String = "UNKNOWN",
    val financialEventType: String = "UNKNOWN",
    val category: String? = null,
    val amount: Double? = null,
    val merchant: String? = null,
    val transactionMode: String = "UNKNOWN",
    val accountName: String? = null,
    val isReviewed: Boolean = false,
    val isFlagged: Boolean = false
)
