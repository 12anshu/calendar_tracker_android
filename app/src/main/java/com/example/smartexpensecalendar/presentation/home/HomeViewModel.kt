package com.example.smartexpensecalendar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.domain.model.Expense
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
    val syncProgress: Float = 0f,
    val lastSyncTime: Long? = null,
    val pendingSyncMonth: YearMonth? = null
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

    private val skippedMonths = mutableSetOf<String>()

    val processedSMSCount: StateFlow<Int> = repository.getProcessedSMSCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            dataStoreManager.selectedMonth.firstOrNull()?.let {
                _selectedMonth.value = it
            }
        }
        observeSyncProgress()
        observeHistoricalSync()
    }

    private fun observeHistoricalSync() {
        dataStoreManager.syncedMonths.onEach { synced ->
            val startMonth = YearMonth.of(2026, 1)
            val currentMonth = YearMonth.now()
            
            var checkMonth = startMonth
            while (!checkMonth.isAfter(currentMonth)) {
                val monthStr = checkMonth.toString()
                if (!synced.contains(monthStr) && !skippedMonths.contains(monthStr)) {
                    _uiState.update { it.copy(pendingSyncMonth = checkMonth) }
                    _uiEvent.send(HomeUiEvent.RequestHistoricalSync(checkMonth))
                    break
                }
                checkMonth = checkMonth.plusMonths(1)
            }
        }.launchIn(viewModelScope)
    }

    fun confirmHistoricalSync(yearMonth: YearMonth) {
        viewModelScope.launch {
            val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.smartexpensecalendar.sms.SMSSyncWorker>()
                .addTag("sms_sync")
                .setInputData(androidx.work.workDataOf(
                    "sync_year" to yearMonth.year,
                    "sync_month" to yearMonth.monthValue
                ))
                .build()
            workManager.enqueue(syncRequest)
            
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
                val isRunning = info.state == androidx.work.WorkInfo.State.RUNNING
                val progress = info.progress.getFloat("progress", 0f)
                _uiState.update { it.copy(isSyncing = isRunning, syncProgress = progress) }
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
        saveMonth()
    }

    fun prevMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
        saveMonth()
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

    private fun saveMonth() {
        viewModelScope.launch {
            dataStoreManager.saveSelectedMonth(_selectedMonth.value)
        }
    }
}
