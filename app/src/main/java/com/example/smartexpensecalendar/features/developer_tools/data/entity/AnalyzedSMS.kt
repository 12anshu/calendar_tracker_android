package com.example.smartexpensecalendar.features.developer_tools.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.model.ExtractionEvidence

@Entity(tableName = "analyzed_sms")
data class AnalyzedSMS(
    @PrimaryKey val id: Long,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val normalizedMessage: String,
    val direction: TransactionDirection,
    val directionConfidence: Int,
    val directionScore: Int,
    val directionEvidence: List<String> = emptyList(),
    val directionEvidenceList: List<ExtractionEvidence> = emptyList(),
    val transactionScore: Int = 0,
    val obligationScore: Int = 0,
    val informationScore: Int = 0,
    val merchantConfidence: Int = 0,
    val merchantScore: Int = 0,
    val merchantEvidence: List<String> = emptyList(),
    val isQualified: Boolean = false,
    val qualificationScore: Int = 0,
    val qualificationConfidence: Int = 0,
    val qualificationEvidence: List<String> = emptyList(),
    val qualificationRules: List<String> = emptyList(),
    val classifiedDirection: String = "UNKNOWN",
    val classifiedDirectionConfidence: Int = 0,
    val classifiedDirectionScore: Int = 0,
    val classifiedDirectionEvidence: List<String> = emptyList(),
    val classifiedDirectionMatches: List<String> = emptyList(),
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
    val entityType: String = "MERCHANT",
    val isReviewed: Boolean = false,
    val isFlagged: Boolean = false
)
