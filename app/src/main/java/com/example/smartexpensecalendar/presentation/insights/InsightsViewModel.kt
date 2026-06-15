package com.example.smartexpensecalendar.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.abs

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

data class VesselSpend(
    val accountName: String,
    val amount: Double,
    val percentage: Float
)

data class DailySpend(
    val date: java.time.LocalDate,
    val amount: Double
)

data class SmartInsight(
    val title: String,
    val description: String,
    val icon: String // key for icon
)

data class Comparison(
    val value: Double,
    val percentChange: Int,
    val isIncrease: Boolean
)

data class InsightsUiState(
    val totalSpent: Double = 0.0,
    val totalIncome: Double = 0.0,
    val spentComparison: Comparison? = null,
    val totalBudget: Double = 0.0,
    val budgetComparison: Comparison? = null,
    val remainingBudget: Double = 0.0,
    val remainingComparison: Comparison? = null,
    
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val topMerchants: List<MerchantSpend> = emptyList(),
    val vesselBreakdown: List<VesselSpend> = emptyList(),
    val dailyTrend: List<DailySpend> = emptyList(),
    val smartInsights: List<SmartInsight> = emptyList(),
    val upiVsCard: Map<String, Double> = emptyMap(),
    
    // Detailed Analytics
    val highestSpendingDay: Pair<java.time.LocalDate, Double>? = null,
    val averageDailySpend: Double = 0.0,
    val mostActiveDay: String = "",
    val peakSpendingTime: String = "",
    val avgTransactionSize: Double = 0.0,
    val p2pVsMerchantSplit: Pair<Int, Int> = 0 to 0, // % split
    
    val isLoading: Boolean = false,
    val selectedMonth: YearMonth = YearMonth.now(),
    val currencySymbol: String = "₹"
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val dataStoreManager: com.example.smartexpensecalendar.data.local.DataStoreManager
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    init {
        observeSelectedMonth()
    }

    private fun observeSelectedMonth() {
        dataStoreManager.selectedMonth.onEach { month ->
            month?.let { _selectedMonth.value = it }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<InsightsUiState> = _selectedMonth
        .flatMapLatest { month ->
            val prevMonth = month.minusMonths(1)
            val symbolFlow = dataStoreManager.currencySymbol
            
            combine(
                repository.getExpensesForMonth(month.year, month.monthValue),
                repository.getBudgetsForMonth(month),
                repository.getExpensesForMonth(prevMonth.year, prevMonth.monthValue),
                repository.getBudgetsForMonth(prevMonth),
                symbolFlow
            ) { expenses, budgets, prevExpenses, prevBudgets, symbol ->
                calculateState(month, expenses, budgets, prevExpenses, prevBudgets, symbol)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())

    private fun calculateState(
        month: YearMonth,
        expenses: List<Expense>,
        budgets: Map<String, Double>,
        prevExpenses: List<Expense>,
        prevBudgets: Map<String, Double>,
        symbol: String
    ): InsightsUiState {
        val debitExpenses = expenses.filter { 
            it.type == TransactionType.DEBIT && it.status == TransactionStatus.COMPLETED 
        }
        val currentTotalSpent = debitExpenses.sumOf { it.amount }

        val creditExpenses = expenses.filter {
            it.type == TransactionType.CREDIT && it.status == TransactionStatus.COMPLETED
        }
        val currentTotalIncome = creditExpenses.sumOf { it.amount }

        val currentTotalBudget = budgets.values.sum()
        
        // Previous Month Comparison
        val prevDebitExpenses = prevExpenses.filter { 
            it.type == TransactionType.DEBIT && it.status == TransactionStatus.COMPLETED 
        }
        val prevTotalSpent = prevDebitExpenses.sumOf { it.amount }
        val prevTotalBudget = prevBudgets.values.sum()

        // 1. Snapshot Comparisons
        val spentComp = calculateComparison(currentTotalSpent, prevTotalSpent)
        val budgetComp = calculateComparison(currentTotalBudget, prevTotalBudget)
        val remainingComp = calculateComparison(
            (currentTotalBudget - currentTotalSpent).coerceAtLeast(0.0),
            (prevTotalBudget - prevTotalSpent).coerceAtLeast(0.0)
        )

        // 2. Category Breakdown
        val catBreakdown = debitExpenses.groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                CategorySpend(cat, sum, if (currentTotalSpent > 0) (sum / currentTotalSpent).toFloat() else 0f)
            }.sortedByDescending { it.amount }

        // 3. Top Merchants
        val topMerchants = debitExpenses.filter { it.merchant != null }
            .groupBy { it.merchant!! }
            .map { (merchant, list) ->
                MerchantSpend(merchant, list.sumOf { it.amount }, list.size)
            }.sortedByDescending { it.amount }.take(5)

        // 4. Daily Trend
        val dailyTrend = debitExpenses.groupBy { it.date }
            .map { (date, list) -> DailySpend(date, list.sumOf { it.amount }) }
            .sortedBy { it.date }

        // 5. UPI vs Card
        val upiTotal = debitExpenses.filter { 
            it.paymentMethod == com.example.smartexpensecalendar.domain.model.PaymentMethod.UPI || 
            it.category == "UPI / Digital" 
        }.sumOf { it.amount }
        val cardTotal = currentTotalSpent - upiTotal

        // 6. Analytics Details
        val highestDay = dailyTrend.maxByOrNull { it.amount }?.let { it.date to it.amount }
        val daysInMonth = month.lengthOfMonth()
        val avgDaily = if (daysInMonth > 0) currentTotalSpent / daysInMonth else 0.0
        
        val activeDay = debitExpenses.groupBy { it.date.dayOfWeek }
            .maxByOrNull { it.value.size }?.key?.name ?: "N/A"
        
        val peakTime = calculatePeakTime(debitExpenses)
        val avgTxn = if (debitExpenses.isNotEmpty()) currentTotalSpent / debitExpenses.size else 0.0
        
        val merchantSpent = debitExpenses.filter { !it.merchant.isNullOrBlank() }.sumOf { it.amount }
        val p2pPercent = if (currentTotalSpent > 0) ((1 - (merchantSpent / currentTotalSpent)) * 100).toInt() else 0

        // 7. Smart Insights
        val insights = mutableListOf<SmartInsight>()
        if (catBreakdown.isNotEmpty()) {
            val topCat = catBreakdown.first()
            insights.add(SmartInsight("${topCat.category} spending", "${(topCat.percentage * 100).toInt()}% of total spending", "category"))
        }
        if (currentTotalSpent > 0) {
            insights.add(SmartInsight("UPI accounts for", "${((upiTotal / currentTotalSpent) * 100).toInt()}% of total spending", "upi"))
        }
        if (topMerchants.isNotEmpty()) {
            insights.add(SmartInsight("${topMerchants.first().merchant} is top", "spent ${symbol}${formatIndianCurrency(topMerchants.first().amount)}", "store"))
        }

        return InsightsUiState(
            totalSpent = currentTotalSpent,
            totalIncome = currentTotalIncome,
            spentComparison = spentComp,
            totalBudget = currentTotalBudget,
            budgetComparison = budgetComp,
            remainingBudget = (currentTotalBudget - currentTotalSpent).coerceAtLeast(0.0),
            remainingComparison = remainingComp,
            categoryBreakdown = catBreakdown,
            topMerchants = topMerchants,
            vesselBreakdown = emptyList(), 
            dailyTrend = dailyTrend,
            smartInsights = insights,
            upiVsCard = mapOf("UPI" to upiTotal, "Card" to cardTotal),
            highestSpendingDay = highestDay,
            averageDailySpend = avgDaily,
            mostActiveDay = activeDay,
            peakSpendingTime = peakTime,
            avgTransactionSize = avgTxn,
            p2pVsMerchantSplit = p2pPercent to (100 - p2pPercent),
            selectedMonth = month,
            currencySymbol = symbol
        )
    }

    private fun calculateComparison(current: Double, previous: Double): Comparison? {
        if (previous <= 0) return null
        val diff = current - previous
        val percent = ((abs(diff) / previous) * 100).toInt()
        return Comparison(current, percent, diff > 0)
    }

    private fun calculatePeakTime(expenses: List<Expense>): String {
        val hourMap = expenses.mapNotNull { it.originalSmsId }
            .map { 
                java.time.Instant.ofEpochMilli(it)
                    .atZone(java.time.ZoneId.systemDefault())
                    .hour 
            }
            .groupingBy { it }.eachCount()
        
        val peakHour = hourMap.maxByOrNull { it.value }?.key ?: return "N/A"
        return "${peakHour}:00 - ${peakHour + 1}:00"
    }

    private fun formatIndianCurrency(amount: Double): String {
        return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))
            .format(amount).replace("₹", "").trim()
    }

    fun setMonth(month: YearMonth) {
        viewModelScope.launch {
            dataStoreManager.saveSelectedMonth(month)
        }
    }
}
