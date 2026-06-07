package com.example.smartexpensecalendar.features.developer_tools.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "misclassified_messages")
data class MisclassifiedMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val sender: String,
    val score: Int,
    val matchedSignals: Set<String>,
    val currentClassification: String,
    val expectedClassification: String,
    val reviewTimestamp: Long
)
