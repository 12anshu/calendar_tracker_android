package com.example.smartexpensecalendar.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    
    /**
     * Formats an amount using the Indian numbering system (Lakhs/Crores)
     * e.g. 1234567.89 -> 12,34,567.89
     */
    fun formatIndianCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formatted = format.format(amount)
        // Remove the default currency symbol added by NumberFormat (₹) to let caller prepend their own
        return formatted.replace("₹", "").replace("Rs.", "").trim()
    }

    /**
     * Formats an amount with a specific currency symbol
     */
    fun formatAmountWithSymbol(amount: Double, symbol: String): String {
        return "$symbol${formatIndianCurrency(amount)}"
    }
}
