package com.example.smartexpensecalendar.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import com.example.smartexpensecalendar.domain.model.MerchantMapping
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseDetailViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val categories: StateFlow<List<String>> = repository.getCustomCategories()
        .map { custom -> DefaultCategories.list + custom }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultCategories.list)

    fun addCustomCategory(name: String) {
        viewModelScope.launch {
            repository.addCustomCategory(name)
        }
    }

    fun getExpensesForDate(date: LocalDate): Flow<List<Expense>> {
        return repository.getExpensesForDate(date)
    }

    fun addExpense(amount: Double, category: String, date: LocalDate) {
        viewModelScope.launch {
            val existing = repository.getExpenseByCategoryAndDate(category, date)
            if (existing != null) {
                repository.upsertExpense(existing.copy(amount = existing.amount + amount))
            } else {
                repository.upsertExpense(
                    Expense(
                        amount = amount,
                        category = category,
                        date = date,
                        merchant = null,
                        source = ExpenseSource.MANUAL
                    )
                )
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
    
    fun updateExpense(expense: Expense, newAmount: Double, newCategory: String, applyToFuture: Boolean = false) {
        viewModelScope.launch {
            repository.upsertExpense(expense.copy(amount = newAmount, category = newCategory))
            
            if (applyToFuture) {
                expense.merchant?.let { merchant ->
                    repository.saveMerchantMapping(MerchantMapping(merchant.lowercase(), newCategory))
                }
            }
        }
    }
}
