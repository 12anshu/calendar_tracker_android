package com.example.smartexpensecalendar.features.developer_tools.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisRepository
import com.example.smartexpensecalendar.features.developer_tools.data.SenderCount
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.data.entity.MisclassifiedMessage
import com.example.smartexpensecalendar.features.developer_tools.domain.model.PatternGroup
import com.example.smartexpensecalendar.features.developer_tools.service.CsvExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SMSAnalysisUiState(
    val totalCount: Int = 0,
    val financialCount: Int = 0,
    val nonFinancialCount: Int = 0,
    val highConfidenceCount: Int = 0,
    val lowConfidenceCount: Int = 0,

    val transactionCount: Int = 0,
    val financialTransactionCount: Int = 0,
    val obligationCount: Int = 0,
    val informationCount: Int = 0,
    val promotionalCount: Int = 0,
    val unknownCount: Int = 0,

    val debitCount: Int = 0,
    val creditCount: Int = 0,
    val unknownDirectionCount: Int = 0,

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
    private val repository: SMSAnalysisRepository,
    private val exportService: CsvExportService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SMSAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _isScoreAsc = MutableStateFlow(false)
    private val _financialFilter = MutableStateFlow<Boolean?>(null)
    private val _messageTypeFilter = MutableStateFlow<String?>(null)
    private val _directionFilter = MutableStateFlow<String?>(null)

    val financialFilter = _financialFilter.asStateFlow()
    val messageTypeFilter = _messageTypeFilter.asStateFlow()
    val directionFilter = _directionFilter.asStateFlow()

    val analyzedSMSPaged: Flow<PagingData<AnalyzedSMS>> = combine(
        _searchQuery, 
        _isScoreAsc, 
        _financialFilter, 
        _messageTypeFilter,
        _directionFilter
    ) { query, isAsc, fin, type, direction ->
        FilterParams(query, isAsc, fin, type, direction)
    }.flatMapLatest { params ->
        repository.getAnalyzedSMSPaged(params.query, params.isAsc, params.financial, params.messageType, params.direction)
    }.cachedIn(viewModelScope)

    private data class FilterParams(
        val query: String,
        val isAsc: Boolean,
        val financial: Boolean?,
        val messageType: String?,
        val direction: String?
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
                repository.getFinancialTransactionCount(),
                repository.getObligationCount(),
                repository.getInformationCount(),
                repository.getPromotionalCount(),
                repository.getUnknownFinancialCount(),
                repository.getDebitCount(),
                repository.getCreditCount(),
                repository.getUnknownDirectionCount(),
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
                    financialTransactionCount = args[10] as Int,
                    obligationCount = args[11] as Int,
                    informationCount = args[12] as Int,
                    promotionalCount = args[13] as Int,
                    unknownCount = args[14] as Int,
                    debitCount = args[15] as Int,
                    creditCount = args[16] as Int,
                    unknownDirectionCount = args[17] as Int,
                    topFinancialKeywords = args[18] as List<Pair<String, Int>>,
                    topNegativeKeywords = args[19] as List<Pair<String, Int>>,
                    topFinancialSenders = args[20] as List<SenderCount>,
                    topNonFinancialSenders = args[21] as List<SenderCount>
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

    fun setDirectionFilter(direction: String?) {
        _directionFilter.value = direction
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

    private val _exportStatus = MutableSharedFlow<String>()
    val exportStatus = _exportStatus.asSharedFlow()

    fun exportToCSV() {
        viewModelScope.launch {
            val messages = repository.getAllAnalyzedSMSList()
            if (messages.isEmpty()) {
                _exportStatus.emit("No messages analyzed yet")
                return@launch
            }
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val data = messages.map { sms ->
                mapOf(
                    "ID" to sms.id.toString(),
                    "Timestamp" to dateFormat.format(Date(sms.timestamp)),
                    "Sender" to sms.sender,
                    "Message" to sms.message,
                    "IsFinancial" to sms.isFinancial.toString(),
                    "Score" to sms.score.toString(),
                    "Confidence" to sms.confidence.toString(),
                    "MessageType" to sms.messageType,
                    "SenderType" to sms.senderType,
                    "MatchedSignals" to sms.matchedSignals.joinToString(";")
                )
            }
            val result = exportService.exportToCsv(data, "AnalyzedSMS")
            _exportStatus.emit(result)
        }
    }

    fun exportDiagnosticData(month: java.time.YearMonth? = null, minimal: Boolean = true) {
        viewModelScope.launch {
            val result = repository.exportDiagnosticData(month, minimal)
            _exportStatus.emit(result)
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
