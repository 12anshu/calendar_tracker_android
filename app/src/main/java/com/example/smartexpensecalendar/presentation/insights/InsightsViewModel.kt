package com.example.smartexpensecalendar.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import javax.inject.Inject

data class CategorySpend(
    val category: String,
    val amount: Double,
    val percentage: Float
)

data class MerchantSpend(
    val merchant: String,
    val amount: Double,
    val count: Int
)

data class InsightsUiState(
    val totalSpent: Double = 0.0,
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val topMerchants: List<MerchantSpend> = emptyList(),
    val upiVsCard: Map<String, Double> = emptyMap(), // "UPI" vs "Card"
    val isLoading: Boolean = false,
    val selectedMonth: YearMonth = YearMonth.now()
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<InsightsUiState> = _selectedMonth.flatMapLatest { month ->
        repository.getExpensesForMonth(month.year, month.monthValue).map { expenses ->
            val debitExpenses = expenses.filter { 
                it.type == TransactionType.DEBIT && it.status == TransactionStatus.COMPLETED 
            }
            
            val total = debitExpenses.sumOf { it.amount }
            
            // 1. Category Breakdown
            val catBreakdown = debitExpenses.groupBy { it.category }
                .map { (cat, list) ->
                    val sum = list.sumOf { it.amount }
                    CategorySpend(cat, sum, if (total > 0) (sum / total).toFloat() else 0f)
                }.sortedByDescending { it.amount }

            // 2. Top Merchants
            val topMerchants = debitExpenses.filter { it.merchant != null }
                .groupBy { it.merchant!! }
                .map { (merchant, list) ->
                    MerchantSpend(merchant, list.sumOf { it.amount }, list.size)
                }.sortedByDescending { it.amount }.take(5)

            // 3. UPI vs Card
            val upiTotal = debitExpenses.filter { 
                it.originalSmsBody?.lowercase()?.contains("upi") == true || it.category == "UPI / Digital" 
            }.sumOf { it.amount }
            val cardTotal = total - upiTotal

            InsightsUiState(
                totalSpent = total,
                categoryBreakdown = catBreakdown,
                topMerchants = topMerchants,
                upiVsCard = mapOf("UPI" to upiTotal, "Card" to cardTotal),
                selectedMonth = month
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())

    fun setMonth(month: YearMonth) {
        _selectedMonth.value = month
    }
}
