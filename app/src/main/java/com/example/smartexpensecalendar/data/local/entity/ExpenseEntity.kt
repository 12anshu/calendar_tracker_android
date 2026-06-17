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
    val financialEventType: String,
    val paymentMethod: String,
    val confidence: Int,
    val source: String,
    val type: String,   // DEBIT or CREDIT
    val status: String, // COMPLETED, SETTLEMENT, REFUNDED, FAILED
    val accountSuffix: String?,
    val accountName: String?,
    val quality: Int = 1,
    val entityType: String = "MERCHANT",
    val linkedId: Long?,
    val originalSmsId: Long?,
    val originalSmsBody: String?,
    val syncDate: Long,
    val createdAt: Long
)
