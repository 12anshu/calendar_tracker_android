package com.example.smartexpensecalendar.presentation.sms_inbox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisDao
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class SmsInboxUiState(
    val smsByDate: Map<LocalDate, List<AnalyzedSMS>> = emptyMap(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val financialFilter: Boolean? = null,
    val messageTypeFilter: String? = null,
    val financialEventTypeFilter: String? = null,
    val directionFilter: String? = null,
    val reviewStatusFilter: ReviewStatus = ReviewStatus.ALL,
    val selectedMonth: YearMonth = YearMonth.now(),
    val financialCount: Int = 0,
    val nonFinancialCount: Int = 0,
    val financialTransactionCount: Int = 0,
    val debitCount: Int = 0,
    val creditCount: Int = 0,
    val unknownDirectionCount: Int = 0
)

enum class ReviewStatus { ALL, PENDING, DONE, FLAGGED }

@HiltViewModel
class SmsInboxViewModel @Inject constructor(
    private val dao: SMSAnalysisDao,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _financialFilter = MutableStateFlow<Boolean?>(null)
    private val _messageTypeFilter = MutableStateFlow<String?>(null)
    private val _financialEventTypeFilter = MutableStateFlow<String?>(null)
    private val _reviewStatusFilter = MutableStateFlow(ReviewStatus.ALL)
    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    private val _directionFilter =
        MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            dataStoreManager.selectedMonth.collect { month ->
                month?.let { _selectedMonth.value = it }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SmsInboxUiState> = combine(
        _selectedMonth,
        _searchQuery,
        _financialFilter,
        _messageTypeFilter,
        _financialEventTypeFilter,
        _reviewStatusFilter,
        _directionFilter
    ) { params ->
        val month = params[0] as YearMonth
        val query = params[1] as String
        val financial = params[2] as Boolean?
        val type = params[3] as String?
        val eventType = params[4] as String?
        val reviewStatus = params[5] as ReviewStatus
        val direction = params[6] as String?

        val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        val isReviewed: Boolean? = when(reviewStatus) {
            ReviewStatus.DONE -> true
            ReviewStatus.PENDING -> false
            else -> null
        }
        val isFlagged: Boolean? = if (reviewStatus == ReviewStatus.FLAGGED) true else null

        combine(
            dao.getFilteredAnalyzedSMS(start, end, query, financial, type, eventType, isReviewed, isFlagged, direction),
            dao.getFinancialSMSCountForMonth(start, end),
            dao.getNonFinancialSMSCountForMonth(start, end),
            dao.getFinancialTransactionCountForMonth(start, end),
            dao.getDirectionCountForMonth(start, end, "DEBIT"),
            dao.getDirectionCountForMonth(start, end, "CREDIT"),
            dao.getDirectionCountForMonth(start, end, "UNKNOWN")
        ) { args ->
            val list = args[0] as List<AnalyzedSMS>
            val finCount = args[1] as Int
            val nonFinCount = args[2] as Int
            val finTxnCount = args[3] as Int
            val debitCount = args[4] as Int
            val creditCount = args[5] as Int
            val unknownDirCount = args[6] as Int

            val grouped = list.groupBy { 
                java.time.Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            SmsInboxUiState(
                smsByDate = grouped,
                searchQuery = query,
                financialFilter = financial,
                messageTypeFilter = type,
                financialEventTypeFilter = eventType,
                directionFilter = direction,
                reviewStatusFilter = reviewStatus,
                selectedMonth = month,
                financialCount = finCount,
                nonFinancialCount = nonFinCount,
                financialTransactionCount = finTxnCount,
                debitCount = debitCount,
                creditCount = creditCount,
                unknownDirectionCount = unknownDirCount
            )
        }
    }.flatMapLatest { it }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SmsInboxUiState())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFinancialFilter(financial: Boolean?) {
        _financialFilter.value = financial
        if (financial == false) _financialEventTypeFilter.value = null
    }

    fun setMessageTypeFilter(type: String?) {
        _messageTypeFilter.value = type
        if (type != "TRANSACTION") _financialEventTypeFilter.value = null
    }

    fun setFinancialEventTypeFilter(type: String?) {
        _financialEventTypeFilter.value = type
    }

    fun setDirectionFilter(direction: String?) {
        _directionFilter.value = direction
    }

    fun setReviewStatusFilter(status: ReviewStatus) {
        _reviewStatusFilter.value = status
    }

    fun toggleReviewStatus(id: Long, current: Boolean) {
        viewModelScope.launch {
            dao.updateReviewStatus(id, !current)
        }
    }

    fun toggleFlagStatus(id: Long, current: Boolean) {
        viewModelScope.launch {
            dao.updateFlagStatus(id, !current)
        }
    }

    fun setMonth(month: YearMonth) {
        viewModelScope.launch {
            dataStoreManager.saveSelectedMonth(month)
        }
    }
}
