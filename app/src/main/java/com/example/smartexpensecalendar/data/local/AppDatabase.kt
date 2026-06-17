package com.example.smartexpensecalendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartexpensecalendar.data.local.entity.BudgetEntity
import com.example.smartexpensecalendar.data.local.entity.CustomCategoryEntity
import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.data.local.entity.MerchantMappingEntity
import com.example.smartexpensecalendar.data.local.entity.SMSLogEntity
import com.example.smartexpensecalendar.data.local.entity.SubscriptionEntity
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.data.entity.MisclassifiedMessage
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisDao
import androidx.room.TypeConverters

@Database(
    entities = [
        ExpenseEntity::class, 
        MerchantMappingEntity::class, 
        SMSLogEntity::class, 
        BudgetEntity::class, 
        CustomCategoryEntity::class,
        SubscriptionEntity::class,

        // DEVELOPER ENTITIES
        AnalyzedSMS::class,
        MisclassifiedMessage::class
    ],
    version = 24,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    // DEVELOPER DAOs
    abstract fun smsAnalysisDao(): SMSAnalysisDao
}
