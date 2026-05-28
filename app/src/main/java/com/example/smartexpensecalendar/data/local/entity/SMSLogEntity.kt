package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_logs")
data class SMSLogEntity(
    @PrimaryKey val smsId: Long,
    val sender: String,
    val body: String,
    val date: Long,
    val status: String,
    val failureReason: String?,
    val parsedAmount: Double? = null,
    val parsedMerchant: String? = null
)
