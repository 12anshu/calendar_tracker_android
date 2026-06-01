package com.example.smartexpensecalendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartexpensecalendar.data.local.entity.BudgetEntity
import com.example.smartexpensecalendar.data.local.entity.CustomCategoryEntity
import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.data.local.entity.MerchantMappingEntity
import com.example.smartexpensecalendar.data.local.entity.SMSLogEntity

@Database(
    entities = [ExpenseEntity::class, MerchantMappingEntity::class, SMSLogEntity::class, BudgetEntity::class, CustomCategoryEntity::class],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
