package com.example.smartexpensecalendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.data.local.entity.MerchantMappingEntity
import com.example.smartexpensecalendar.data.local.entity.SMSLogEntity

@Database(
    entities = [ExpenseEntity::class, MerchantMappingEntity::class, SMSLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
