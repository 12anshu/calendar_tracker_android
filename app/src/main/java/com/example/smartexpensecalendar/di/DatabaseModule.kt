package com.example.smartexpensecalendar.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_expense.db"
        ).fallbackToDestructiveMigration()
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
