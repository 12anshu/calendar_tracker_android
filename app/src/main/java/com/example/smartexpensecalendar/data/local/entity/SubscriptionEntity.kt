package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartexpensecalendar.domain.model.SubscriptionTier

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String, // e.g., "monthly_pro", "yearly_pro", "lifetime_elite"
    val name: String,
    val tier: SubscriptionTier,
    val price: Double,
    val currency: String,
    val validityDays: Int, // 30, 365, or -1 for lifetime
    val features: List<String>, // List of feature keys/descriptions
    val isActive: Boolean = false,
    val expiryDate: Long? = null
)
