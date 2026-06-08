package com.example.smartexpensecalendar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ProcessingStatus
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.utils.ExportUtils
import com.example.smartexpensecalendar.utils.ImportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

sealed class HomeUiEvent {
    data class ExportFile(val content: String, val fileName: String, val mimeType: String) : HomeUiEvent()
    object TriggerImport : HomeUiEvent()
    data class ShowError(val message: String) : HomeUiEvent()
    data class RequestHistoricalSync(val yearMonth: YearMonth) : HomeUiEvent()
}

data class HomeUiState(
    val isSyncing: Boolean = false,
    val isCurrentMonthSynced: Boolean = true,
    val syncProgress: Float = 0f,
    val totalRead: Int = 0,
    val expensesFound: Int = 0,
    val lastSyncTime: Long? = null,
    val pendingSyncMonth: YearMonth? = null,
    val totalBudget: Double = 0.0,
    val previousMonthTotal: Double = 0.0,
    val currencySymbol: String = "₹"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val dataStoreManager: DataStoreManager,
    private val workManager: androidx.work.WorkManager
) : ViewModel() {

    private val _uiEvent = Channel<HomeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val syncSummary: StateFlow<SyncSummary?> = _selectedMonth.flatMapLatest { month ->
        repository.getSMSLogsForMonth(month.year, month.monthValue).map { logs ->
            if (logs.isEmpty()) return@map null
            
            val financial = logs.filter { it.status == ProcessingStatus.PROCESSED }
            SyncSummary(
                totalSmsScanned = logs.size,
                financialSmsFound = financial.size,
                totalAmount = financial.sumOf { it.parsedAmount ?: 0.0 },
                logs = financial
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val skippedMonths = mutableSetOf<String>()

    val processedSMSCount: StateFlow<Int> = repository.getProcessedSMSCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notifications: StateFlow<List<SMSProcessingLog>> = repository.getAllSMSLogs()
        .map { logs -> 
            logs.filter { it.status == ProcessingStatus.SYNC_COMPLETE || it.status == ProcessingStatus.PROCESSED } 
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearSMSLogs()
        }
    }

    init {
        observeSelectedMonth()
        observeSyncProgress()
        observeMonthSyncStatus()
        observeBudget()
        observeCurrency()
    }

    private fun observeSelectedMonth() {
        dataStoreManager.selectedMonth.onEach { month ->
            month?.let { _selectedMonth.value = it }
        }.launchIn(viewModelScope)
    }

    private fun observeCurrency() {
        dataStoreManager.currencySymbol.onEach { symbol ->
            _uiState.update { it.copy(currencySymbol = symbol) }
        }.launchIn(viewModelScope)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBudget() {
        _selectedMonth.flatMapLatest { month ->
            repository.getBudgetsForMonth(month)
        }.onEach { budgets ->
            val total = budgets["Total"] ?: 0.0
            _uiState.update { it.copy(totalBudget = total) }
        }.launchIn(viewModelScope)
    }

    private fun observeMonthSyncStatus() {
        combine(_selectedMonth, dataStoreManager.syncedMonths) { month, synced ->
            synced.contains(month.toString())
        }.onEach { isSynced ->
            _uiState.update { it.copy(isCurrentMonthSynced = isSynced) }
        }.launchIn(viewModelScope)
    }

    fun syncSelectedMonth() {
        confirmHistoricalSync(_selectedMonth.value)
    }

    fun resetAndSync() {
        viewModelScope.launch {
            val currentMonth = _selectedMonth.value
            repository.clearMonthData(currentMonth)
            dataStoreManager.clearSyncStatusForMonth(currentMonth.toString())
            syncSelectedMonth()
        }
    }

    fun confirmHistoricalSync(yearMonth: YearMonth) {
        viewModelScope.launch {
            val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.smartexpensecalendar.sms.SMSSyncWorker>()
                .addTag("sms_sync")
                .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(androidx.work.workDataOf(
                    "sync_year" to yearMonth.year,
                    "sync_month" to yearMonth.monthValue
                ))
                .build()
            
            workManager.enqueueUniqueWork(
                "monthly_sms_sync",
                androidx.work.ExistingWorkPolicy.REPLACE,
                syncRequest
            )
            
            _uiState.update { it.copy(pendingSyncMonth = null) }
        }
    }

    fun dismissHistoricalSync() {
        _uiState.value.pendingSyncMonth?.let {
            skippedMonths.add(it.toString())
        }
        _uiState.update { it.copy(pendingSyncMonth = null) }
    }

    private fun observeSyncProgress() {
        workManager.getWorkInfosByTagLiveData("sms_sync").asFlow().onEach { workInfos ->
            val info = workInfos.firstOrNull()
            if (info != null) {
                val isRunning = info.state == androidx.work.WorkInfo.State.RUNNING || 
                               info.state == androidx.work.WorkInfo.State.ENQUEUED
                
                val progressData = if (info.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                    info.outputData
                } else {
                    info.progress
                }

                val progress = progressData.getFloat("progress", 0f)
                val totalRead = progressData.getInt("total_read", 0)
                val expensesFound = progressData.getInt("expenses_found", 0)
                
                _uiState.update { it.copy(
                    isSyncing = isRunning, 
                    syncProgress = progress,
                    totalRead = totalRead,
                    expensesFound = expensesFound
                ) }
            } else {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenses: StateFlow<List<Expense>> = combine(_selectedMonth, _searchQuery) { month, query ->
        month to query
    }.flatMapLatest { (month, query) ->
        repository.getExpensesForMonth(month.year, month.monthValue).map { list ->
            if (query.isBlank()) list
            else list.filter { it.category.contains(query, ignoreCase = true) || it.merchant?.contains(query, ignoreCase = true) == true }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val previousMonthTotal: StateFlow<Double> = _selectedMonth.flatMapLatest { month ->
        val prev = month.minusMonths(1)
        repository.getExpensesForMonth(prev.year, prev.monthValue).map { list ->
            list.filter { 
                it.type == com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT &&
                it.status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED
            }.sumOf { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun nextMonth() {
        saveMonth(_selectedMonth.value.plusMonths(1))
    }

    fun prevMonth() {
        saveMonth(_selectedMonth.value.minusMonths(1))
    }

    fun setMonth(yearMonth: YearMonth) {
        saveMonth(yearMonth)
    }

    fun exportCSV() {
        val content = ExportUtils.toCSV(expenses.value)
        viewModelScope.launch {
            _uiEvent.send(HomeUiEvent.ExportFile(content, "expenses_${_selectedMonth.value}.csv", "text/csv"))
        }
    }

    fun exportJSON() {
        val content = ExportUtils.toJSON(expenses.value)
        viewModelScope.launch {
            _uiEvent.send(HomeUiEvent.ExportFile(content, "expenses_${_selectedMonth.value}.json", "application/json"))
        }
    }

    fun triggerImport() {
        viewModelScope.launch {
            _uiEvent.send(HomeUiEvent.TriggerImport)
        }
    }

    fun importJSON(jsonString: String) {
        viewModelScope.launch {
            try {
                val importedExpenses = ImportUtils.fromJSON(jsonString)
                importedExpenses.forEach { expense ->
                    val existing = repository.getExpenseByCategoryAndDate(expense.category, expense.date)
                    if (existing != null) {
                        repository.upsertExpense(existing.copy(amount = existing.amount + expense.amount))
                    } else {
                        repository.upsertExpense(expense.copy(id = 0))
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(HomeUiEvent.ShowError("Failed to import: ${e.message}"))
            }
        }
    }

    private fun saveMonth(month: YearMonth) {
        viewModelScope.launch {
            dataStoreManager.saveSelectedMonth(month)
        }
    }
}

data class SyncSummary(
    val totalSmsScanned: Int,
    val financialSmsFound: Int,
    val totalAmount: Double,
    val logs: List<SMSProcessingLog>
)
