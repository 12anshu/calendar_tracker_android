package com.example.smartexpensecalendar.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class CategoryBudgetState(
    val category: String,
    val spent: Double,
    val budget: Double
)

data class BudgetUiState(
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val categoryBudgets: List<CategoryBudgetState> = emptyList(),
    val currencySymbol: String = "₹"
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val dataStoreManager: com.example.smartexpensecalendar.data.local.DataStoreManager
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    init {
        observeSelectedMonth()
    }

    private fun observeSelectedMonth() {
        dataStoreManager.selectedMonth.onEach { month ->
            month?.let { _selectedMonth.value = it }
        }.launchIn(viewModelScope)
    }

    val categories: StateFlow<List<String>> = repository.getCustomCategories()
        .map { custom -> DefaultCategories.list + custom }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultCategories.list)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetUiState> = combine(
        _selectedMonth,
        repository.getCustomCategories(),
        dataStoreManager.currencySymbol
    ) { month, customCats, symbol -> Triple(month, (DefaultCategories.list + customCats), symbol) }
    .flatMapLatest { (month, categories, symbol) ->
        repository.getBudgetsForMonth(month)
            .flatMapLatest { budgets ->
                repository.getExpensesForMonth(month.year, month.monthValue).map { expenses ->
                    val debitExpenses = expenses.filter { 
                        it.type == TransactionType.DEBIT && it.status == TransactionStatus.COMPLETED
                    }

                    val totalBudget = budgets["Total"] ?: 0.0
                    val totalSpent = debitExpenses.sumOf { it.amount }

                    val categoryStates = categories.map { category ->
                        val spent = debitExpenses.filter { it.category == category }.sumOf { it.amount }
                        val budget = budgets[category] ?: 0.0
                        CategoryBudgetState(category, spent, budget)
                    }

                    BudgetUiState(totalBudget, totalSpent, categoryStates, symbol)
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetUiState())

    fun setMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            dataStoreManager.saveSelectedMonth(yearMonth)
        }
    }

    fun updateBudget(category: String, amount: Double) {
        viewModelScope.launch {
            repository.upsertBudget(_selectedMonth.value, category, amount)
        }
    }

    fun addCustomCategory(name: String, initialBudget: Double = 0.0) {
        viewModelScope.launch {
            repository.addCustomCategory(name)
            if (initialBudget > 0) {
                repository.upsertBudget(_selectedMonth.value, name, initialBudget)
            }
        }
    }
}
