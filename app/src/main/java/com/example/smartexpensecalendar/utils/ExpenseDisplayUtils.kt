package com.example.smartexpensecalendar.utils

import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionType

object ExpenseDisplayUtils {

    /**
     * Centralized logic to get the primary display name for a transaction.
     * Prioritizes Merchant -> Account -> Event Type fallback.
     */
    fun getDisplayName(expense: Expense): String {
        val rawName = when {
            !expense.merchant.isNullOrBlank() -> expense.merchant
            !expense.accountName.isNullOrBlank() -> getVesselDisplay(expense.accountName)
            expense.financialEventType == FinancialEventType.TRANSFER -> "Account Transfer"
            expense.financialEventType == FinancialEventType.EMI_PAYMENT -> "EMI Payment"
            expense.financialEventType == FinancialEventType.EMI_CONVERSION -> "EMI Conversion"
            expense.financialEventType == FinancialEventType.CASH_WITHDRAWAL -> "Cash Withdrawal"
            expense.financialEventType == FinancialEventType.CREDIT_CARD_PAYMENT -> "Card Payment"
            expense.financialEventType == FinancialEventType.INVESTMENT -> "Investment"
            expense.financialEventType == FinancialEventType.REFUND -> "Refund"
            expense.financialEventType == FinancialEventType.SALARY -> "Salary"
            expense.financialEventType == FinancialEventType.CASHBACK -> "Cashback"
            expense.financialEventType == FinancialEventType.MEAL_CARD -> "Meal Card"
            expense.type == TransactionType.DEBIT -> "Payment"
            else -> "Received"
        }
        return rawName.uppercase()
    }

    /**
     * Unifies the display of a financial vessel (Bank Account/Card).
     * Converts "DBS A/C 9490" -> "DBS BANK A/C 9490" or "DBS BANK [A/C 9490]"
     */
    fun getVesselDisplay(accountName: String?): String {
        if (accountName.isNullOrBlank()) return "UNKNOWN SOURCE"
        
        var display = accountName.uppercase()
            .replace("A/C", "A/C")
            .trim()

        // If it contains a bank name but not "BANK", add it (optional, but makes it consistent)
        // If it doesn't have [ ] around A/C, let's add it if we want that specific premium look
        if (display.contains("A/C") && !display.contains("[A/C")) {
            display = display.replace("A/C", "[A/C") + "]"
            // Fix trailing ] if it already had one or if we added it in the middle
            display = display.replace("]]", "]")
        }

        return display
    }
}
