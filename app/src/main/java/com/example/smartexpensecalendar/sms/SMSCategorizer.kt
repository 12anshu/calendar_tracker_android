package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.sms.merchant.MerchantNormalizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSCategorizer @Inject constructor(
    private val repository: ExpenseRepository
) {

    val defaultMappings = mapOf(

        // Food
        "swiggy" to "Food",
        "zomato" to "Food",
        "dominos" to "Food",
        "kfc" to "Food",
        "mcdonalds" to "Food",
        "eatclub" to "Food",
        "faasos" to "Food",
        "behrouz" to "Food",
        "starbucks" to "Food",

        // Travel
        "uber" to "Travel",
        "ola" to "Travel",
        "rapido" to "Travel",
        "irctc" to "Travel",
        "makemytrip" to "Travel",

        // Shopping
        "amazon" to "Online Shopping",
        "flipkart" to "Online Shopping",
        "myntra" to "Online Shopping",
        "ajio" to "Online Shopping",

        // Groceries
        "bigbasket" to "Groceries",
        "blinkit" to "Groceries",
        "zepto" to "Groceries",
        "dmart" to "Groceries",
        "reliance fresh" to "Groceries",

        // Utilities
        "airtel" to "Bill Payment",
        "jio" to "Bill Payment",
        "vi" to "Bill Payment",
        "electricity" to "Utilities",
        "water" to "Utilities",
        "gas" to "Utilities",

        // Fuel
        "fuel" to "Fuel",
        "petrol" to "Fuel",
        "shell" to "Fuel",
        "hpcl" to "Fuel",
        "iocl" to "Fuel",
        "indianoil" to "Fuel",
        "bharat petroleum" to "Fuel",

        // Medical
        "apollo" to "Medical",
        "hospital" to "Medical",
        "pharmacy" to "Medical",
        "1mg" to "Medical",
        "netmeds" to "Medical",
        "pharmeasy" to "Medical",

        // Entertainment
        "netflix" to "Entertainment",
        "spotify" to "Entertainment",
        "hotstar" to "Entertainment",
        "prime video" to "Entertainment",
        "youtube" to "Entertainment",
        "bookmyshow" to "Entertainment",
        "pvr" to "Entertainment",

        // Rent
        "rent" to "Rent",
        "flat" to "Rent",

        // Transfers
        "neft" to "Transfer",
        "ach" to "Transfer",
        "imps" to "Transfer",
        "rtgs" to "Transfer",

        // Meal Card
        "meal card" to "Food"
    )

    suspend fun categorize(merchant: String?): String {

        if (merchant.isNullOrBlank())
            return "Miscellaneous"

        val normalized =
            MerchantNormalizer.normalize(merchant)!!

        // User mapping first
        val savedCategory =
            repository.getCategoryForMerchant(
                normalized
            )

        if (savedCategory != null)
            return savedCategory

        // Default mappings
        defaultMappings[normalized]?.let {
            return it
        }

        // Contains fallback
        for ((keyword, category) in defaultMappings) {

            if (normalized.contains(keyword))
                return category
        }

        return "Miscellaneous"
    }
}