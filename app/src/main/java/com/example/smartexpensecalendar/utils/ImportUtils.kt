package com.example.smartexpensecalendar.utils

import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import org.json.JSONObject
import java.time.LocalDate

object ImportUtils {
    fun fromJSON(jsonString: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val root = JSONObject(jsonString)
        val array = root.getJSONArray("expenses")
        
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            expenses.add(
                Expense(
                    amount = obj.getDouble("amount"),
                    category = obj.getString("category"),
                    date = LocalDate.parse(obj.getString("date")),
                    merchant = obj.optString("merchant", null),
                    source = ExpenseSource.valueOf(obj.getString("source")),
                    originalSmsId = if (obj.has("originalSmsId")) obj.getLong("originalSmsId") else null,
                    createdAt = if (obj.has("createdAt")) obj.getLong("createdAt") else System.currentTimeMillis()
                )
            )
        }
        return expenses
    }
}
