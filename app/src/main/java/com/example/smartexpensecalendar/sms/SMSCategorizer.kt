package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSCategorizer @Inject constructor(
    private val repository: ExpenseRepository
) {

    /**
     * Categorizes a transaction using a priority-based logic:
     * 1. Status-based (Refund, Settlement, etc.)
     * 2. Event Type-based (EMI, Investment, etc.)
     * 3. Manual User Overrides
     * 4. Direct Merchant Registry Match
     * 5. Keyword-based fallback
     */
    suspend fun categorize(
        merchant: String?,
        eventType: com.example.smartexpensecalendar.domain.model.FinancialEventType = com.example.smartexpensecalendar.domain.model.FinancialEventType.UNKNOWN,
        status: com.example.smartexpensecalendar.domain.model.TransactionStatus = com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED,
        paymentMethod: com.example.smartexpensecalendar.domain.model.PaymentMethod = com.example.smartexpensecalendar.domain.model.PaymentMethod.UNKNOWN
    ): String {
        // --- 1. STATUS-BASED CATEGORIES ---
        if (status == com.example.smartexpensecalendar.domain.model.TransactionStatus.REFUNDED) return "Refund"
        if (status == com.example.smartexpensecalendar.domain.model.TransactionStatus.SETTLEMENT) return "Settlement"
        
        // --- 2. EVENT TYPE-BASED CATEGORIES ---
        when (eventType) {
            com.example.smartexpensecalendar.domain.model.FinancialEventType.INCOME -> return "Income"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.EMI_PAYMENT -> return "EMI & Loans"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.INVESTMENT -> return "Investment"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.TRANSFER -> return "Transfer"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.CREDIT_CARD_PAYMENT -> return "Card Payment"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.CASH_WITHDRAWAL -> return "Cash Withdrawal"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.CASH_DEPOSIT -> return "Cash Deposit"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.EMI_CONVERSION -> return "EMI Conversion"
            com.example.smartexpensecalendar.domain.model.FinancialEventType.MEAL_CARD -> return "Meal Card"
            else -> {}
        }

        if (merchant.isNullOrBlank()) {
            return when {
                eventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.INCOME -> "Income"
                paymentMethod == com.example.smartexpensecalendar.domain.model.PaymentMethod.UPI -> "UPI / Digital"
                // New logic: All generic credits are "Income", generic debits are "Payment"
                eventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.UNKNOWN && status == com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED -> "Payment"
                else -> "Miscellaneous"
            }
        }

        val normalized = MerchantNormalizer.normalize(merchant) ?: merchant
        val upperMerchant = normalized.uppercase()

        // --- 3. USER OVERRIDES ---
        val savedCategory = repository.getCategoryForMerchant(normalized)
        if (savedCategory != null) return savedCategory

        // --- 4. MERCHANT REGISTRY MATCH ---
        val registryMatch = com.example.smartexpensecalendar.sms.config.MerchantRegistry.merchants.find { definition ->
            definition.canonicalName.uppercase() == upperMerchant || 
            definition.aliases.any { it == upperMerchant }
        }
        if (registryMatch != null) return registryMatch.category

        // --- 5. KEYWORD-BASED MATCHING (FALLBACK) ---
        return when {
            containsAny(upperMerchant, setOf("FUEL", "PETROL", "SHELL", "HPCL", "IOCL", "INDIANOIL", "BHARAT PETROLEUM", "BPCL")) -> "Fuel"
            containsAny(upperMerchant, setOf("RENT", "FLAT", "MAINTENANCE", "HOUSING", "MYGATE", "NOBROKER")) -> "Rent & Maintenance"
            containsAny(upperMerchant, setOf("HOSPITAL", "PHARMACY", "CLINIC", "DOCTOR", "HEALTHCARE", "DIAGNOSTIC", "MEDPLUS", "APOLLO")) -> "Medical"
            containsAny(upperMerchant, setOf("NETFLIX", "SPOTIFY", "DISNEY", "HOTSTAR", "YOUTUBE", "PRIME", "TICKET", "PVR", "INOX")) -> "Entertainment"
            containsAny(upperMerchant, setOf("AIRTEL", "JIO", "VODAFONE", "IDEA", "ELECTRICITY", "WATER", "GAS", "BESCOM", "RECHARGE")) -> "Bill Payment"
            containsAny(upperMerchant, setOf("AMAZON", "FLIPKART", "MYNTRA", "AJIO", "RETAIL", "FASHION", "ZIVAME", "NYKAA", "LIFESTYLE", "PANTALOONS", "MAX")) -> "Shopping"
            containsAny(upperMerchant, setOf("UBER", "OLA", "RAPIDO", "METRO", "RAIL", "IRCTC", "FLIGHT", "MAKEMYTRIP", "INDIGO", "AIR INDIA")) -> "Travel"
            containsAny(upperMerchant, setOf("SWIGGY", "ZOMATO", "FOOD", "RESTAURANT", "CAFE", "BAKERY", "EATCLUB", "DOMINOS", "PIZZA", "BURGER", "KFC", "MCDONALD")) -> "Food"
            containsAny(upperMerchant, setOf("BIGBASKET", "BLINKIT", "ZEPTO", "DMART", "GROCERY", "SUPERMARKET", "RELIANCE FRESH", "JIOMART", "BBDAILY")) -> "Groceries"
            containsAny(upperMerchant, setOf("INVEST", "MUTUAL", "SIP", "STOCK", "GROWW", "ZERODHA", "TRADING", "COIN", "SMALLCASE", "INDMONEY")) -> "Investment"
            paymentMethod == com.example.smartexpensecalendar.domain.model.PaymentMethod.UPI -> "UPI / Digital"
            eventType == com.example.smartexpensecalendar.domain.model.FinancialEventType.INCOME -> "Money Received"
            else -> "Payment"
        }
    }

    private fun containsAny(text: String, keywords: Set<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
}
