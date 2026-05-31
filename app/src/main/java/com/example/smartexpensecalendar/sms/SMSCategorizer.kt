package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSCategorizer @Inject constructor(
    private val repository: ExpenseRepository
) {
    val defaultMappings = mapOf(
        "swiggy" to "Food",
        "zomato" to "Food",
        "bundl" to "Food",
        "uber" to "Travel",
        "ola" to "Travel",
        "amazon" to "Online Shopping",
        "flipkart" to "Online Shopping",
        "bigbasket" to "Groceries",
        "blinkit" to "Groceries",
        "zepto" to "Groceries",
        "cheq" to "Bill Payment",
        "airtel" to "Bill Payment",
        "jio" to "Bill Payment",
        "vi " to "Bill Payment",
        "electricity" to "Utilities",
        "water" to "Utilities",
        "gas" to "Utilities",
        "petrol" to "Fuel",
        "fuel" to "Fuel",
        "shell" to "Fuel",
        "hospital" to "Medical",
        "pharmacy" to "Medical",
        "apollo" to "Medical",
        "netflix" to "Entertainment",
        "spotify" to "Entertainment",
        "bookmyshow" to "Entertainment",
        "pvr" to "Entertainment",
        "flat" to "Rent",
        "rent" to "Rent",
        "neft" to "Transfer",
        "ach" to "Transfer",
        "upi" to "UPI / Digital",
        "vpa" to "UPI / Digital",
        "meal card" to "Groceries"
    )

    suspend fun categorize(merchant: String?): String {
        if (merchant == null) return "Miscellaneous"

        val merchantLower = merchant.lowercase()

        // 0. Special Hardcoded Rules
        if (merchantLower.contains("neft") || merchantLower.contains("ach")) return "Transfer"
        if (merchantLower.contains("meal card")) return "Groceries"

        // 1. Check user-defined mappings in DB
        val savedCategory = repository.getCategoryForMerchant(merchantLower)
        if (savedCategory != null) return savedCategory

        // 2. Check default keyword mappings
        for ((keyword, category) in defaultMappings) {
            if (merchantLower.contains(keyword)) return category
        }

        return "Miscellaneous"
    }
}
