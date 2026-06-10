package com.example.smartexpensecalendar.sms.reconciliation

import android.util.Log
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class TransactionLinker @Inject constructor(
    private val repository: ExpenseRepository
) {

    /**
     * Systematic Reconciliation Engine.
     * Triggered primarily by Credits to find corresponding Debits within a 48h window.
     */
    suspend fun linkTransactions(startDate: LocalDate, endDate: LocalDate) {
        // Fetch all unlinked candidates in range
        val credits = repository.findExpensesInRange(TransactionType.CREDIT, startDate, endDate)
            .filter { it.linkedId == null && it.status != TransactionStatus.FAILED }

        // We fetch debits with a small buffer before start date to handle cross-month/latency pairs
        val availableDebits = repository.findExpensesInRange(TransactionType.DEBIT, startDate.minusDays(2), endDate)
            .filter { it.linkedId == null && it.status != TransactionStatus.FAILED }
            .toMutableList()

        if (credits.isEmpty() || availableDebits.isEmpty()) return

        credits.forEach { credit ->
            val match = findBestMovementMatch(credit, availableDebits)
            if (match != null) {
                applyForcedLink(match, credit)
                availableDebits.remove(match)
            }
        }
    }

    private fun findBestMovementMatch(credit: Expense, debits: List<Expense>): Expense? {
        // Priority 1: Same Day + Exact Amount
        debits.find { 
            it.date == credit.date && 
            isExactAmount(it.amount, credit.amount) &&
            isCompatibleForLinking(it, credit)
        }?.let { return it }

        // Priority 2: 48h Window + Exact Amount
        debits.find { 
            isWithinWindow(it.date, credit.date, 2) && 
            isExactAmount(it.amount, credit.amount) &&
            isCompatibleForLinking(it, credit)
        }?.let { return it }

        // Priority 3: 48h Window + Fuzzy Amount (for CC Bill Payments with discounts)
        debits.find { 
            isWithinWindow(it.date, credit.date, 2) && 
            isFuzzyAmountMatch(it.amount, credit.amount) &&
            isCompatibleForLinking(it, credit)
        }?.let { return it }

        return null
    }

    private fun isExactAmount(a: Double, b: Double): Boolean = abs(a - b) < 0.01

    private fun isFuzzyAmountMatch(a: Double, b: Double): Boolean {
        val diff = abs(a - b)
        val percent = (diff / a) * 100
        return percent <= 2.0 || diff <= 50.0
    }

    /**
     * The Gatekeeper: Ensures we only link internal movements or specific refunds.
     */
    private fun isCompatibleForLinking(debit: Expense, credit: Expense): Boolean {
        // --- 1. MOVEMENT INTENT (Barrier Removal) ---
        // If either side is already confirmed as a Transfer or CC Payment via Enum.
        val isConfirmedMovement = 
            credit.financialEventType == FinancialEventType.TRANSFER || 
            credit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT
                    ||
            debit.financialEventType == FinancialEventType.TRANSFER ||
            debit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT

        // If intent is confirmed, categories don't matter. Match them.
        if (isConfirmedMovement) return true

        // --- 2. MERCHANT LOCK (Refund Protection) ---
        // For generic spending, merchants must match exactly (e.g. Swiggy refund).
        val dMerchant = debit.merchant?.uppercase()
        val cMerchant = credit.merchant?.uppercase()

        if (!dMerchant.isNullOrBlank() && !cMerchant.isNullOrBlank()) {
            return dMerchant == cMerchant
        }

        // --- 3. GENERIC FALLBACK (India Context) ---
        // If both have no merchant name (common in P2P), allow link.
        if (dMerchant.isNullOrBlank() && cMerchant.isNullOrBlank()) {
            return true
        }

        return false
    }

    private suspend fun applyForcedLink(debit: Expense, credit: Expense) {
        // 1. Unify the Category
        val finalCategory = when {
            debit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT ||
            credit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT -> "Card Payment"
            
            debit.financialEventType == FinancialEventType.EMI_PAYMENT || 
            credit.financialEventType == FinancialEventType.EMI_PAYMENT -> "EMI & Loans"
            
            else -> "Transfer"
        }

        // 2. Identity Merge: Choose the most descriptive merchant name
        // (Filter out placeholders like "Payment", "Received", "Account Transfer")
        val dMerchant = debit.merchant
        val cMerchant = credit.merchant
        
        val enrichedMerchant = when {
            finalCategory == "Card Payment" && !cMerchant.isNullOrBlank() -> cMerchant
            !dMerchant.isNullOrBlank() && dMerchant != "Payment" && dMerchant != "Account Transfer" -> dMerchant
            !cMerchant.isNullOrBlank() && cMerchant != "Received" -> cMerchant
            else -> dMerchant ?: cMerchant
        }

        // 3. Full Object Update: Force Room to refresh the UI with new status and metadata
        val settledDebit = debit.copy(
            merchant = enrichedMerchant,
            category = finalCategory,
            status = TransactionStatus.SETTLEMENT,
            linkedId = credit.id
        )
        val settledCredit = credit.copy(
            merchant = enrichedMerchant,
            category = finalCategory,
            status = TransactionStatus.SETTLEMENT,
            linkedId = debit.id
        )

        repository.upsertExpense(settledDebit)
        repository.upsertExpense(settledCredit)
        
        Log.d("TransactionLinker", "RECONCILED: ${debit.amount} -> $finalCategory ($enrichedMerchant)")
    }

    private fun isWithinWindow(d1: LocalDate, d2: LocalDate, days: Int): Boolean {
        return abs(d1.toEpochDay() - d2.toEpochDay()) <= days
    }
}
