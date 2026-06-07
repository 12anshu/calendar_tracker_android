package com.example.smartexpensecalendar.features.developer_tools.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisRepository
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionExtraction
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms_engine.extractor.AmountExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantExtractor
import com.example.smartexpensecalendar.features.developer_tools.service.CsvExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionExtractionViewModel @Inject constructor(
    private val repository: SMSAnalysisRepository,
    private val exportService: CsvExportService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtractionUiState())
    val uiState = _uiState.asStateFlow()

    val filteredResults = _uiState.map { state ->
        val baseResults = when (state.selectedFilter) {
            ExtractionFilter.ALL -> state.results
            ExtractionFilter.FAILED -> state.results.filter { it.extraction.amount == null }
            ExtractionFilter.MERCHANT_MISSING ->
                state.results.filter {
                    it.extraction.merchant.isNullOrBlank() &&
                            it.extraction.eventType in setOf(
                        FinancialEventType.EXPENSE,
                        FinancialEventType.CREDIT_CARD_SPEND
                    )
                }
            ExtractionFilter.UNKNOWN_EVENT -> state.results.filter { it.extraction.eventType == FinancialEventType.UNKNOWN }
            ExtractionFilter.LOW_CONFIDENCE -> state.results.filter { it.extraction.confidence < 70 }
            ExtractionFilter.EXPENSE -> state.results.filter { it.extraction.eventType == FinancialEventType.EXPENSE }
            ExtractionFilter.INCOME -> state.results.filter { it.extraction.eventType == FinancialEventType.INCOME }
            ExtractionFilter.TRANSFER -> state.results.filter { it.extraction.eventType == FinancialEventType.TRANSFER }
            ExtractionFilter.REFUND -> state.results.filter { it.extraction.eventType == FinancialEventType.REFUND }
            ExtractionFilter.CC_SPEND -> state.results.filter { it.extraction.eventType == FinancialEventType.CREDIT_CARD_SPEND }
        }
        
        if (state.merchantSearchQuery.isBlank()) {
            baseResults
        } else {
            baseResults.filter { 
                it.extraction.merchant?.contains(state.merchantSearchQuery, ignoreCase = true) == true 
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: ExtractionFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun setMerchantSearchQuery(query: String) {
        _uiState.update { it.copy(merchantSearchQuery = query) }
    }
    
    fun toggleSenderSection() {
        _uiState.update { it.copy(isSenderSectionExpanded = !it.isSenderSectionExpanded) }
    }
    
    fun toggleResultExpansion(result: ExtractionResult) {
        _uiState.update { state ->
            val updatedResults = state.results.map { 
                if (it.sms.id == result.sms.id) it.copy(isExpanded = !it.isExpanded) else it 
            }
            state.copy(results = updatedResults)
        }
    }

    private val _exportStatus = MutableSharedFlow<String>()
    val exportStatus = _exportStatus.asSharedFlow()

    fun runExtraction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }
            
            val smsList = repository.getTransactionSMS()
            val results = smsList.map { sms ->
                val amount = AmountExtractor.extractAmount(sms.message)
                val mode = ModeExtractor.extractMode(sms.message)
                val direction = DirectionExtractor.extractDirection(sms.message)
                val merchant = MerchantExtractor.extractMerchant(sms.message)

                var confidence = 0
                if (amount != null) confidence += 40
                if (direction != TransactionDirection.UNKNOWN) confidence += 20
                if (mode != TransactionMode.UNKNOWN) confidence += 20
                if (!merchant.isNullOrBlank()) confidence += 20

                val eventType = FinancialEventTypeExtractor.extract(
                    smsText = sms.message,
                    direction = direction,
                    mode = mode
                )

                val extraction = TransactionExtraction(
                    amount = amount,
                    merchant = merchant,
                    mode = mode,
                    direction = direction,
                    confidence = confidence,
                    eventType = eventType
                )
                
                ExtractionResult(sms, extraction)
            }
            
            val total = results.size
            val amountCount = results.count { it.extraction.amount != null }
            val merchantApplicableEvents = setOf(
                FinancialEventType.EXPENSE,
                FinancialEventType.CREDIT_CARD_SPEND
            )

            val merchantApplicableCount =
                results.count {
                    it.extraction.eventType in merchantApplicableEvents
                }

            val merchantCount =
                results.count {
                    it.extraction.eventType in merchantApplicableEvents &&
                            !it.extraction.merchant.isNullOrBlank()
                }

            val directionCount = results.count { it.extraction.direction != TransactionDirection.UNKNOWN }
            val modeCount = results.count { it.extraction.mode != TransactionMode.UNKNOWN }
            val eventTypeCount = results.count { it.extraction.eventType != FinancialEventType.UNKNOWN }
            
            // Distributions
            val eventTypeDist = results.groupingBy { it.extraction.eventType.name }.eachCount()
            
            val confDist = mutableMapOf(
                "100%" to results.count { it.extraction.confidence == 100 },
                "90-99%" to results.count { it.extraction.confidence in 90..99 },
                "80-89%" to results.count { it.extraction.confidence in 80..89 },
                "70-79%" to results.count { it.extraction.confidence in 70..79 },
                "Below 70%" to results.count { it.extraction.confidence < 70 }
            )
            
            val senderStats = results.groupingBy { it.sms.sender }.eachCount()
                .toList().sortedByDescending { it.second }.take(10)
            
            val merchants = results.mapNotNull { it.extraction.merchant }
            val uniqueMerchantCount = merchants.distinct().count()
            val topMerchants = merchants.groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(10)

            _uiState.update { 
                it.copy(
                    totalTransactions = total,
                    amountExtractedCount = amountCount,
                    extractionFailedCount = total - amountCount,
                    merchantExtractedCount = merchantCount,
                    merchantMissingCount = merchantApplicableCount - merchantCount,
                    uniqueMerchantCount = uniqueMerchantCount,
                    topMerchants = topMerchants,
                    eventTypeDistribution = eventTypeDist,
                    confidenceDistribution = confDist,
                    topSenders = senderStats,
                    amountExtractionRate = if (total > 0) amountCount.toFloat() / total else 0f,
                    merchantCoverage =
                        if (merchantApplicableCount > 0)
                            merchantCount.toFloat() /
                                    merchantApplicableCount
                        else
                            0f,
                    directionDetectionRate = if (total > 0) directionCount.toFloat() / total else 0f,
                    modeDetectionRate = if (total > 0) modeCount.toFloat() / total else 0f,
                    eventTypeDetectionRate = if (total > 0) eventTypeCount.toFloat() / total else 0f,
                    isRunning = false,
                    results = results
                )
            }
        }
    }

    fun exportToCSV() {
        viewModelScope.launch {
            val results = _uiState.value.results
            if (results.isEmpty()) {
                _exportStatus.emit("No transactions found")
                return@launch
            }
            val data = results.map { res ->
                mapOf(
                    "Sender" to res.sms.sender,
                    "Amount" to (res.extraction.amount?.toString() ?: "N/A"),
                    "Merchant" to (res.extraction.merchant ?: "Not Detected"),
                    "Direction" to res.extraction.direction.name,
                    "Mode" to res.extraction.mode.name,
                    "EventType" to res.extraction.eventType.name,
                    "Confidence" to res.extraction.confidence.toString(),
                    "Message" to res.sms.message
                )
            }
            val result = exportService.exportToCsv(data, "TransactionExtraction")
            _exportStatus.emit(result)
        }
    }
    
    fun exportFiltered(filterType: String) {
        viewModelScope.launch {
            val allResults = _uiState.value.results
            val filtered = when(filterType) {
                "FAILED" -> allResults.filter { it.extraction.amount == null }
                "MERCHANT_MISSING" ->
                    allResults.filter {
                        it.extraction.merchant.isNullOrBlank() &&
                                it.extraction.eventType in setOf(
                            FinancialEventType.EXPENSE,
                            FinancialEventType.CREDIT_CARD_SPEND
                        )
                    }
                "UNKNOWN_EVENT" -> allResults.filter { it.extraction.eventType == FinancialEventType.UNKNOWN }
                "LOW_CONFIDENCE" -> allResults.filter { it.extraction.confidence < 70 }
                else -> emptyList()
            }
            
            if (filtered.isEmpty()) {
                _exportStatus.emit("No data to export for $filterType")
                return@launch
            }
            
            val data = filtered.map { res ->
                mapOf(
                    "Sender" to res.sms.sender,
                    "Amount" to (res.extraction.amount?.toString() ?: "N/A"),
                    "Merchant" to (res.extraction.merchant ?: "Not Detected"),
                    "Direction" to res.extraction.direction.name,
                    "Mode" to res.extraction.mode.name,
                    "EventType" to res.extraction.eventType.name,
                    "Confidence" to res.extraction.confidence.toString(),
                    "Message" to res.sms.message
                )
            }
            val result = exportService.exportToCsv(data, "Export_$filterType")
            _exportStatus.emit(result)
        }
    }
}
