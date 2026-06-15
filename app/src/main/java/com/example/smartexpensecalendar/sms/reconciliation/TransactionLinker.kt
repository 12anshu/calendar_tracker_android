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
        // --- 1. CLEANUP PHASE (Brute Force Reset) ---
        // We reset everything in the buffer window (e.g. 5 days around range) 
        // to handle cross-month or delayed SMS logic correctly.
        val searchStart = startDate.minusDays(5)
        val searchEnd = endDate.plusDays(2)

        val allInRange = repository.findExpensesInRange(TransactionType.CREDIT, searchStart, searchEnd) +
                        repository.findExpensesInRange(TransactionType.DEBIT, searchStart, searchEnd)
        
        allInRange.forEach { expense ->
            if (expense.linkedId != null || expense.status == TransactionStatus.SETTLEMENT) {
                repository.updateExpenseStatus(expense.id, TransactionStatus.COMPLETED, null)
            }
        }

        // --- 2. MATCHING PHASE ---
        val credits = repository.findExpensesInRange(TransactionType.CREDIT, searchStart, searchEnd)
            .filter { it.status != TransactionStatus.FAILED }

        val availableDebits = repository.findExpensesInRange(TransactionType.DEBIT, searchStart, searchEnd)
            .filter { it.status != TransactionStatus.FAILED }
            .toMutableList()

        if (credits.isEmpty() || availableDebits.isEmpty()) return

        credits.forEach { credit ->
            // 1. Try Exact Match First
            val exactMatch = findExactMatch(credit, availableDebits)
            if (exactMatch != null) {
                applyForcedLink(exactMatch, credit, TransactionStatus.SETTLEMENT)
                availableDebits.remove(exactMatch)
            } else {
                // 2. Try Fuzzy Match (Requires User Confirmation)
                val fuzzyMatch = findFuzzyMatch(credit, availableDebits)
                if (fuzzyMatch != null) {
                    applyForcedLink(fuzzyMatch, credit, TransactionStatus.PENDING_REVIEW)
                    availableDebits.remove(fuzzyMatch)
                }
            }
        }
    }

    private fun findExactMatch(credit: Expense, debits: List<Expense>): Expense? {
        return debits.find { 
            isWithinWindow(it.date, credit.date, 2) && 
            isExactAmount(it.amount, credit.amount) &&
            isCompatibleForLinking(it, credit)
        }
    }

    private fun findFuzzyMatch(credit: Expense, debits: List<Expense>): Expense? {
        return debits.find { 
            isWithinWindow(it.date, credit.date, 2) && 
            isFuzzyAmountMatch(it.amount, credit.amount) &&
            isCompatibleForLinking(it, credit)
        }
    }

    private fun isExactAmount(a: Double, b: Double): Boolean = abs(a - b) < 0.01

    private fun isFuzzyAmountMatch(a: Double, b: Double): Boolean {
        val diff = abs(a - b)
        val percent = (diff / a) * 100
        return percent <= 2.0 || diff <= 50.0
    }

    /**
     * Logic: Internal movements don't need merchant matching.
     * Spend/Refunds DO need merchant matching.
     */
    private fun isCompatibleForLinking(debit: Expense, credit: Expense): Boolean {
        // --- 1. MOVEMENT INTENT (Barrier Removal) ---
        // If either side is already confirmed as a Transfer or CC Payment via Enum.
        // We prioritize this check to allow linking even if merchant names are null/different.
        val isConfirmedMovement = 
            credit.financialEventType == FinancialEventType.TRANSFER || 
            credit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT ||
            debit.financialEventType == FinancialEventType.TRANSFER ||
            debit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT

        // If intent is confirmed, we trust the Amount + Time window 100%.
        if (isConfirmedMovement) return true

        // --- 2. MERCHANT LOCK (Refund Protection) ---
        // For generic spending, merchants must match exactly (e.g. Swiggy refund).
        val dMerchant = debit.merchant?.uppercase()
        val cMerchant = credit.merchant?.uppercase()

        if (!dMerchant.isNullOrBlank() && !cMerchant.isNullOrBlank()) {
            return dMerchant == cMerchant
        }

        // --- 3. GENERIC FALLBACK (India Context) ---
        // If one or both have no merchant name (common in P2P/generic bank debits), allow link.
        if (dMerchant.isNullOrBlank() || cMerchant.isNullOrBlank()) {
            return true
        }

        return false
    }

    private suspend fun applyForcedLink(debit: Expense, credit: Expense, targetStatus: TransactionStatus) {
        // 1. Unify the Category
        val finalCategory = when {
            debit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT || 
            credit.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT -> "Card Payment"
            
            debit.financialEventType == FinancialEventType.EMI_PAYMENT || 
            credit.financialEventType == FinancialEventType.EMI_PAYMENT -> "EMI & Loans"
            
            else -> "Transfer"
        }

        // 2. Identity Merge
        val dMerchant = debit.merchant
        val cMerchant = credit.merchant
        
        val enrichedMerchant = when {
            finalCategory == "Card Payment" && !cMerchant.isNullOrBlank() && cMerchant != "NONE" -> cMerchant
            !dMerchant.isNullOrBlank() && dMerchant != "Payment" && dMerchant != "Account Transfer" && dMerchant != "NONE" -> dMerchant
            !cMerchant.isNullOrBlank() && cMerchant != "Received" && cMerchant != "NONE" -> cMerchant
            else -> if (dMerchant == "NONE" || dMerchant == "Payment") cMerchant else dMerchant ?: cMerchant
        }

        val finalMerchant = if (enrichedMerchant == "NONE") null else enrichedMerchant

        // 3. Full Update Cycle: Status -> Metadata -> Upsert
        // This triple-check ensures Room definitely notifies the UI for BOTH IDs
        repository.updateExpenseStatus(debit.id, targetStatus, credit.id)
        repository.updateExpenseStatus(credit.id, targetStatus, debit.id)
        
        repository.updateExpenseCategory(debit.id, finalCategory)
        repository.updateExpenseCategory(credit.id, finalCategory)

        val updatedDebit = debit.copy(
            status = targetStatus, 
            category = finalCategory, 
            merchant = finalMerchant, 
            linkedId = credit.id
        )
        val updatedCredit = credit.copy(
            status = targetStatus, 
            category = finalCategory, 
            merchant = finalMerchant, 
            linkedId = debit.id
        )
        
        repository.upsertExpense(updatedDebit)
        repository.upsertExpense(updatedCredit)
        
        Log.d("TransactionLinker", "RECONCILED PAIR: ${debit.id}(D) <-> ${credit.id}(C) | Amount: ${debit.amount} | Status: $targetStatus")
    }

    private fun isWithinWindow(d1: LocalDate, d2: LocalDate, days: Int): Boolean {
        return abs(d1.toEpochDay() - d2.toEpochDay()) <= days
    }
}
