package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["originalSmsId"], unique = true)]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: String, // Stored as ISO string (Transaction date)
    val merchant: String?,
    val source: String,
    val originalSmsId: Long?,
    val originalSmsBody: String?, // For testing/debugging
    val syncDate: Long, // Date when it was synced
    val createdAt: Long // Record creation timestamp
)
