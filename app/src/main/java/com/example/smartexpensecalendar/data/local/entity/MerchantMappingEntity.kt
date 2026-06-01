package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchant_mappings")
data class MerchantMappingEntity(
    @PrimaryKey val merchantKeyword: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)
