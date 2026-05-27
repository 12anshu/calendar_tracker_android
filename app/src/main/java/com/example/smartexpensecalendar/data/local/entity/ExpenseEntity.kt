package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: String, // Stored as ISO string
    val merchant: String?,
    val source: String,
    val originalSmsId: Long?,
    val createdAt: Long
)
