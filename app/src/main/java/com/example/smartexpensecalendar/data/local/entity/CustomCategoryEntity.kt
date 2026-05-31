package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey val name: String
)
