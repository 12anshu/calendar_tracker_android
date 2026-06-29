package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Category related vocabulary.
 *
 * NOTE:
 * These signals are used only as fallback evidence during
 * categorization. Merchant Intelligence always has higher priority.
 */
object CategorySignals {

    /**
     * Food & Dining
     */
    val FOOD_SIGNALS = setOf(
        "FOOD",
        "RESTAURANT",
        "CAFE",
        "HOTEL",
        "DINING",
        "MEAL",
        "LUNCH",
        "DINNER",
        "BREAKFAST",
        "BAKERY",
        "PIZZA",
        "BURGER",
        "COFFEE"
    )

    /**
     * Grocery
     */
    val GROCERY_SIGNALS = setOf(
        "GROCERY",
        "SUPERMARKET",
        "MART",
        "STORE",
        "VEGETABLE",
        "FRUITS",
        "DAIRY",
        "PROVISION"
    )

    /**
     * Shopping
     */
    val SHOPPING_SIGNALS = setOf(
        "SHOPPING",
        "RETAIL",
        "FASHION",
        "CLOTHING",
        "APPAREL",
        "ELECTRONICS",
        "FOOTWEAR",
        "ACCESSORIES"
    )

    /**
     * Fuel
     */
    val FUEL_SIGNALS = setOf(
        "FUEL",
        "PETROL",
        "DIESEL",
        "CNG",
        "GAS STATION"
    )

    /**
     * Travel & Transport
     */
    val TRAVEL_SIGNALS = setOf(
        "TRAVEL",
        "CAB",
        "TAXI",
        "AUTO",
        "METRO",
        "BUS",
        "TRAIN",
        "FLIGHT",
        "AIRLINE",
        "TOLL",
        "PARKING"
    )

    /**
     * Bills & Utilities
     */
    val BILL_PAYMENT_SIGNALS = setOf(
        "ELECTRICITY",
        "WATER",
        "GAS",
        "BROADBAND",
        "MOBILE",
        "POSTPAID",
        "RECHARGE",
        "DTH",
        "UTILITY"
    )

    /**
     * Healthcare
     */
    val MEDICAL_SIGNALS = setOf(
        "HOSPITAL",
        "CLINIC",
        "DOCTOR",
        "MEDICAL",
        "MEDICINE",
        "PHARMACY",
        "LAB",
        "DIAGNOSTIC"
    )

    /**
     * Entertainment
     */
    val ENTERTAINMENT_SIGNALS = setOf(
        "MOVIE",
        "CINEMA",
        "OTT",
        "MUSIC",
        "GAME",
        "SUBSCRIPTION",
        "STREAMING"
    )

    /**
     * Investment
     */
    val INVESTMENT_SIGNALS = setOf(
        "INVESTMENT",
        "SIP",
        "MUTUAL FUND",
        "STOCK",
        "SHARE",
        "ETF",
        "FD",
        "RD",
        "NPS",
        "PPF"
    )

    /**
     * Loan
     */
    val LOAN_SIGNALS = setOf(
        "LOAN",
        "EMI",
        "INSTALLMENT",
        "MORTGAGE",
        "FINANCE"
    )

    /**
     * Insurance
     */
    val INSURANCE_SIGNALS = setOf(
        "INSURANCE",
        "PREMIUM",
        "POLICY",
        "COVER"
    )

    /**
     * Rewards & Benefits
     */
    val REWARD_SIGNALS = setOf(
        "REWARD",
        "REWARDS",
        "POINTS",
        "MILES",
        "CASHBACK",
        "BENEFIT"
    )
}