package com.example.smartexpensecalendar.data.local

import androidx.room.TypeConverter
import com.example.smartexpensecalendar.domain.model.SubscriptionTier

class Converters {
    @TypeConverter
    fun fromSubscriptionTier(tier: SubscriptionTier): String {
        return tier.name
    }

    @TypeConverter
    fun toSubscriptionTier(tier: String): SubscriptionTier {
        return SubscriptionTier.valueOf(tier)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split(",")
    }
}
