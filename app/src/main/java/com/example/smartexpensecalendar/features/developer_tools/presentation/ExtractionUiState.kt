package com.example.smartexpensecalendar.features.developer_tools.presentation

import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.domain.model.TransactionExtraction

data class ExtractionUiState(
    val totalTransactions: Int = 0,
    val amountExtractedCount: Int = 0,
    val extractionFailedCount: Int = 0,
    val merchantExtractedCount: Int = 0,
    val merchantMissingCount: Int = 0,
    val uniqueMerchantCount: Int = 0,
    val topMerchants: List<Pair<String, Int>> = emptyList(),
    val merchantSearchQuery: String = "",
    val isRunning: Boolean = false,
    val selectedFilter: ExtractionFilter = ExtractionFilter.ALL,
    val results: List<ExtractionResult> = emptyList(),
    
    // Section 1: Event Type Analytics
    val eventTypeDistribution: Map<String, Int> = emptyMap(),
    
    // Section 2: Confidence Analytics
    val confidenceDistribution: Map<String, Int> = emptyMap(),
    
    // Section 4: Sender Analytics
    val topSenders: List<Pair<String, Int>> = emptyList(),
    val isSenderSectionExpanded: Boolean = false,
    
    // Section 5: Extraction Quality Metrics
    val amountExtractionRate: Float = 0f,
    val merchantCoverage: Float = 0f,
    val directionDetectionRate: Float = 0f,
    val modeDetectionRate: Float = 0f,
    val eventTypeDetectionRate: Float = 0f
)

enum class ExtractionFilter {
    ALL, 
    FAILED, 
    MERCHANT_MISSING, 
    UNKNOWN_EVENT, 
    LOW_CONFIDENCE, 
    EXPENSE, 
    INCOME, 
    TRANSFER, 
    REFUND, 
    CC_SPEND
}

data class ExtractionResult(
    val sms: AnalyzedSMS,
    val extraction: TransactionExtraction,
    val isExpanded: Boolean = false
)
