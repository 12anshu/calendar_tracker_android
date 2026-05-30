package com.example.smartexpensecalendar.utils
import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatIndianCurrency(amount: Double): String {
        val formatter =
            NumberFormat.getNumberInstance(Locale("en", "IN"))

        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0

        return formatter.format(amount)
    }
}