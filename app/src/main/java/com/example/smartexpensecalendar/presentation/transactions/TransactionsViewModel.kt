package com.example.smartexpensecalendar.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class TransactionsUiState(
    val transactions: Map<LocalDate, List<Expense>> = emptyMap(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedType: TransactionType? = null,
    val selectedMonth: YearMonth = YearMonth.now()
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    val selectedType = _selectedType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TransactionsUiState> = combine(
        _selectedMonth, _searchQuery, _selectedCategory, _selectedType
    ) { month, query, category, type ->
        DataQuery(month, query, category, type)
    }.flatMapLatest { query ->
        repository.getExpensesForMonth(query.month.year, query.month.monthValue)
            .map { list ->
                val filtered = list.filter { expense ->
                    val matchesQuery = query.query.isBlank() || 
                        expense.merchant?.contains(query.query, ignoreCase = true) == true ||
                        expense.category.contains(query.query, ignoreCase = true)
                    
                    val matchesCategory = query.category == null || expense.category == query.category
                    val matchesType = query.type == null || expense.type == query.type
                    
                    matchesQuery && matchesCategory && matchesType
                }.sortedByDescending { it.date }
                
                TransactionsUiState(
                    transactions = filtered.groupBy { it.date },
                    searchQuery = query.query,
                    selectedCategory = query.category,
                    selectedType = query.type,
                    selectedMonth = query.month
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMonth(month: YearMonth) {
        _selectedMonth.value = month
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setType(type: TransactionType?) {
        _selectedType.value = type
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    private data class DataQuery(
        val month: YearMonth,
        val query: String,
        val category: String?,
        val type: TransactionType?
    )
}
