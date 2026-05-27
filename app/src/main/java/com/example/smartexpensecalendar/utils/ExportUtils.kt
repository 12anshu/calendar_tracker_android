package com.example.smartexpensecalendar.utils

import com.example.smartexpensecalendar.domain.model.Expense
import org.json.JSONArray
import org.json.JSONObject

object ExportUtils {
    fun toCSV(expenses: List<Expense>): String {
        val sb = StringBuilder()
        sb.append("date,category,amount,source\n")
        for (expense in expenses) {
            sb.append("${expense.date},${expense.category},${expense.amount},${expense.source}\n")
        }
        return sb.toString()
    }

    fun toJSON(expenses: List<Expense>): String {
        val root = JSONObject()
        val array = JSONArray()
        for (expense in expenses) {
            val obj = JSONObject()
            obj.put("date", expense.date.toString())
            obj.append("category", expense.category)
            obj.put("amount", expense.amount)
            obj.put("source", expense.source.name)
            obj.put("merchant", expense.merchant ?: "")
            obj.put("originalSmsId", expense.originalSmsId ?: -1)
            obj.put("createdAt", expense.createdAt)
            array.put(obj)
        }
        root.put("expenses", array)
        root.put("exportDate", System.currentTimeMillis())
        root.put("version", 1)
        return root.toString(4)
    }
}
