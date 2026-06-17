package com.example.smartexpensecalendar.domain.model

enum class EntityType {
    MERCHANT,      // Swiggy, Amazon, Zepto, Uber
    MEAL_CARD,     // Axis Meal Card, Pluxee, Sodexo, Zeta
    ACCOUNT,       // HDFC Bank [A/C 1147]
    TRANSFER,      // NEFT, IMPS, RTGS transfers
    CARD_PAYMENT,  // SBI Card Payment, HDFC Credit Card Payment
    SYSTEM         // Cashback, Statement, Reward messages
}
