package com.example.smartexpensecalendar.developer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.smartexpensecalendar.developer.data.SMSAnalysisRepository
import com.example.smartexpensecalendar.developer.data.SenderCount
import com.example.smartexpensecalendar.developer.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.developer.data.entity.MisclassifiedMessage
import com.example.smartexpensecalendar.developer.domain.model.PatternGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SMSAnalysisUiState(
    val totalCount: Int = 0,
    val financialCount: Int = 0,
    val nonFinancialCount: Int = 0,
    val highConfidenceCount: Int = 0,
    val lowConfidenceCount: Int = 0,
    val transactionCount: Int = 0,
    val obligationCount: Int = 0,
    val informationCount: Int = 0,
    val promotionalCount: Int = 0,
    val unknownCount: Int = 0,
    val isAnalyzing: Boolean = false,
    val progress: Float = 0f,
    val patternGroups: List<PatternGroup> = emptyList(),
    val borderlineMessages: List<AnalyzedSMS> = emptyList(),
    val potentialMisclassifications: List<AnalyzedSMS> = emptyList(),
    val failedCases: List<MisclassifiedMessage> = emptyList(),
    val topFinancialKeywords: List<Pair<String, Int>> = emptyList(),
    val topNegativeKeywords: List<Pair<String, Int>> = emptyList(),
    val topFinancialSenders: List<SenderCount> = emptyList(),
    val topNonFinancialSenders: List<SenderCount> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SMSAnalysisViewModel @Inject constructor(
    private val repository: SMSAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SMSAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _isScoreAsc = MutableStateFlow(false)
    private val _financialFilter = MutableStateFlow<Boolean?>(null)
    private val _messageTypeFilter = MutableStateFlow<String?>(null)

    val financialFilter = _financialFilter.asStateFlow()
    val messageTypeFilter = _messageTypeFilter.asStateFlow()

    val analyzedSMSPaged: Flow<PagingData<AnalyzedSMS>> = combine(
        _searchQuery, 
        _isScoreAsc, 
        _financialFilter, 
        _messageTypeFilter
    ) { query, isAsc, fin, type ->
        FilterParams(query, isAsc, fin, type)
    }.flatMapLatest { params ->
        repository.getAnalyzedSMSPaged(params.query, params.isAsc, params.financial, params.messageType)
    }.cachedIn(viewModelScope)

    private data class FilterParams(
        val query: String,
        val isAsc: Boolean,
        val financial: Boolean?,
        val messageType: String?
    )

    init {
        viewModelScope.launch {
            combine(
                repository.getAnalyzedSMSCount(),
                repository.getFinancialSMSCount(),
                repository.getNonFinancialSMSCount(),
                repository.getPatternGroups(),
                repository.getBorderlineMessages(),
                repository.getPotentialMisclassifications(),
                repository.getMisclassifiedMessages(),
                repository.getHighConfidenceFinancialCount(),
                repository.getLowConfidenceFinancialCount(),
                repository.getTransactionCount(),
                repository.getObligationCount(),
                repository.getInformationCount(),
                repository.getPromotionalCount(),
                repository.getUnknownFinancialCount(),
                repository.getTopFinancialKeywords(),
                repository.getTopNegativeKeywords(),
                repository.getTopFinancialSenders(),
                repository.getTopNonFinancialSenders()
            ) { args: Array<Any> ->
                SMSAnalysisUiState(
                    totalCount = args[0] as Int,
                    financialCount = args[1] as Int,
                    nonFinancialCount = args[2] as Int,
                    patternGroups = args[3] as List<PatternGroup>,
                    borderlineMessages = args[4] as List<AnalyzedSMS>,
                    potentialMisclassifications = args[5] as List<AnalyzedSMS>,
                    failedCases = args[6] as List<MisclassifiedMessage>,
                    highConfidenceCount = args[7] as Int,
                    lowConfidenceCount = args[8] as Int,
                    transactionCount = args[9] as Int,
                    obligationCount = args[10] as Int,
                    informationCount = args[11] as Int,
                    promotionalCount = args[12] as Int,
                    unknownCount = args[13] as Int,
                    topFinancialKeywords = args[14] as List<Pair<String, Int>>,
                    topNegativeKeywords = args[15] as List<Pair<String, Int>>,
                    topFinancialSenders = args[16] as List<SenderCount>,
                    topNonFinancialSenders = args[17] as List<SenderCount>
                )
            }.collect { 
                _uiState.value = it
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleScoreSort() {
        _isScoreAsc.value = !_isScoreAsc.value
    }

    fun isScoreAsc() = _isScoreAsc.value

    fun setFinancialFilter(financial: Boolean?) {
        _financialFilter.value = financial
    }

    fun setMessageTypeFilter(type: String?) {
        _messageTypeFilter.value = type
    }

    fun runFullAnalysis() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, progress = 0f) }
            repository.runFullAnalysis { p ->
                _uiState.update { it.copy(progress = p) }
            }
            _uiState.update { it.copy(isAnalyzing = false) }
        }
    }

    fun flagMisclassification(sms: AnalyzedSMS, expectedClassification: String) {
        viewModelScope.launch {
            repository.flagMisclassification(sms, expectedClassification)
        }
    }
    
    fun exportFailedCases(): String {
        val cases = _uiState.value.failedCases
        val json = com.google.gson.Gson().toJson(cases.map { 
            mapOf(
                "sms" to it.message, 
                "currentClassification" to it.currentClassification,
                "expectedClassification" to it.expectedClassification
            )
        })
        return json
    }

}
