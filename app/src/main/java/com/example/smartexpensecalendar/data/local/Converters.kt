package com.example.smartexpensecalendar.data.local

import androidx.room.TypeConverter
import com.example.smartexpensecalendar.domain.model.SubscriptionTier
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.model.ExtractionEvidence
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    fun fromTransactionDirection(direction: TransactionDirection): String {
        return direction.name
    }

    @TypeConverter
    fun toTransactionDirection(direction: String): TransactionDirection {
        return try {
            TransactionDirection.valueOf(direction)
        } catch (e: Exception) {
            TransactionDirection.UNKNOWN
        }
    }

    @TypeConverter
    fun fromExtractionEvidenceList(value: List<ExtractionEvidence>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toExtractionEvidenceList(value: String): List<ExtractionEvidence> {
        val listType = object : TypeToken<List<ExtractionEvidence>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split(",")
    }

    // DEVELOPER CONVERTERS (Set<String>)
    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        return if (value.isBlank()) emptySet() else value.split(",").toSet()
    }

    @TypeConverter
    fun fromMap(value: Map<String, Int>): String {
        return value.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    @TypeConverter
    fun toMap(value: String): Map<String, Int> {
        if (value.isBlank()) return emptyMap()
        return value.split(",").associate {
            val lastColonIndex = it.lastIndexOf(':')
            if (lastColonIndex != -1) {
                val key = it.substring(0, lastColonIndex)
                val valueStr = it.substring(lastColonIndex + 1)
                key to (valueStr.toIntOrNull() ?: 0)
            } else {
                it to 0
            }
        }
    }
}
