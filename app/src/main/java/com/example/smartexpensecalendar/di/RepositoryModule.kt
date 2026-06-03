package com.example.smartexpensecalendar.di

import com.example.smartexpensecalendar.data.repository.ExpenseRepositoryImpl
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: com.example.smartexpensecalendar.data.repository.AuthRepositoryImpl
    ): com.example.smartexpensecalendar.domain.repository.AuthRepository
}
