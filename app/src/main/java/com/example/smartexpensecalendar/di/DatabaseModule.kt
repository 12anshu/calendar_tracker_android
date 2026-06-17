package com.example.smartexpensecalendar.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartexpensecalendar.data.local.AppDatabase
import com.example.smartexpensecalendar.data.local.ExpenseDao
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Add column to expenses
            db.execSQL("ALTER TABLE expenses ADD COLUMN entityType TEXT NOT NULL DEFAULT 'MERCHANT'")
            
            // 2. Add column to analyzed_sms
            db.execSQL("ALTER TABLE analyzed_sms ADD COLUMN entityType TEXT NOT NULL DEFAULT 'MERCHANT'")

            // 3. Backfill 'expenses' table
            db.execSQL("""
                UPDATE expenses SET entityType = 'MEAL_CARD' 
                WHERE merchant LIKE '%MEAL CARD%' OR merchant LIKE '%PLUXEE%' OR merchant LIKE '%SODEXO%' OR merchant LIKE '%ZETA%'
            """)
            db.execSQL("UPDATE expenses SET entityType = 'ACCOUNT' WHERE merchant LIKE '%[A/C%'")
            db.execSQL("UPDATE expenses SET entityType = 'TRANSFER' WHERE merchant LIKE '%NEFT%' OR merchant LIKE '%IMPS%' OR merchant LIKE '%RTGS%'")
            db.execSQL("UPDATE expenses SET entityType = 'CARD_PAYMENT' WHERE merchant LIKE '%CARD PAYMENT%' OR merchant LIKE '%CREDIT CARD PAYMENT%'")
            db.execSQL("UPDATE expenses SET entityType = 'SYSTEM' WHERE merchant IS NULL OR merchant LIKE '%STATEMENT%' OR merchant LIKE '%CASHBACK%'")

            // 4. Backfill 'analyzed_sms' table
            db.execSQL("""
                UPDATE analyzed_sms SET entityType = 'MEAL_CARD' 
                WHERE merchant LIKE '%MEAL CARD%' OR merchant LIKE '%PLUXEE%' OR merchant LIKE '%SODEXO%' OR merchant LIKE '%ZETA%'
            """)
            db.execSQL("UPDATE analyzed_sms SET entityType = 'ACCOUNT' WHERE merchant LIKE '%[A/C%'")
            db.execSQL("UPDATE analyzed_sms SET entityType = 'TRANSFER' WHERE merchant LIKE '%NEFT%' OR merchant LIKE '%IMPS%' OR merchant LIKE '%RTGS%'")
            db.execSQL("UPDATE analyzed_sms SET entityType = 'CARD_PAYMENT' WHERE merchant LIKE '%CARD PAYMENT%' OR merchant LIKE '%CREDIT CARD PAYMENT%'")
            db.execSQL("UPDATE analyzed_sms SET entityType = 'SYSTEM' WHERE merchant IS NULL OR merchant LIKE '%STATEMENT%' OR merchant LIKE '%CASHBACK%'")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_expense.db"
        ).addMigrations(MIGRATION_23_24)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    // DEVELOPER MODULE DAOs
    @Provides
    fun provideSMSAnalysisDao(database: AppDatabase): SMSAnalysisDao {
        return database.smsAnalysisDao()
    }
}
