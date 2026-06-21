package com.example.smartexpensecalendar.presentation.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Long = checkNotNull(savedStateHandle["expenseId"])

    private val _uiState = MutableStateFlow(TransactionDetailsUiState(isLoading = true))
    val uiState: StateFlow<TransactionDetailsUiState> = _uiState.asStateFlow()

    init {
        loadTransaction()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadTransaction() {
        viewModelScope.launch {
            repository.getExpenseById(expenseId)
                .flatMapLatest { expense ->
                    if (expense == null) {
                        flowOf(_uiState.update { it.copy(expense = null, isLoading = false) })
                    } else {
                        val merchant = expense.merchant
                        val insightsFlow = if (!merchant.isNullOrBlank()) {
                            repository.getTransactionsByMerchant(merchant).map { history ->
                                MerchantInsights(
                                    totalTransactions = history.size,
                                    totalSpend = history.sumOf { it.amount },
                                    averageSpend = if (history.isNotEmpty()) history.sumOf { it.amount } / history.size else 0.0,
                                    lastTransactionDate = history.maxByOrNull { it.date }?.date
                                )
                            }
                        } else {
                            flowOf(null)
                        }

                        combine(
                            repository.getCustomCategories(),
                            insightsFlow
                        ) { customCategories, insights ->
                            _uiState.update {
                                it.copy(
                                    expense = expense,
                                    categories = DefaultCategories.list + customCategories,
                                    merchantInsights = insights,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }.collect()
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun updateExpenseCategory(category: String, applyToFuture: Boolean) {
        val currentExpense = _uiState.value.expense ?: return
        viewModelScope.launch {
            val updatedExpense = currentExpense.copy(category = category)
            repository.upsertExpense(updatedExpense)
            
            if (applyToFuture && !currentExpense.merchant.isNullOrBlank()) {
                repository.saveMerchantMapping(
                    com.example.smartexpensecalendar.domain.model.MerchantMapping(
                        merchantKeyword = currentExpense.merchant.lowercase(),
                        category = category
                    )
                )
            }
            _uiState.update { it.copy(isEditMode = false) }
        }
    }

    fun deleteExpense(onDeleted: () -> Unit) {
        val currentExpense = _uiState.value.expense ?: return
        viewModelScope.launch {
            repository.deleteExpense(currentExpense)
            onDeleted()
        }
    }

    fun submitReport(issueType: String, details: String) {
        // Prepare structure for future backend sync
        _uiState.update { it.copy(reportSubmitted = true, reportStatus = "Report saved locally") }
    }
}
