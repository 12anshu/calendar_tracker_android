package com.example.smartexpensecalendar.data.mapper

import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = LocalDate.parse(date),
        merchant = merchant,
        source = ExpenseSource.valueOf(source),
        originalSmsId = originalSmsId,
        originalSmsBody = originalSmsBody,
        syncDate = syncDate,
        createdAt = createdAt
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        date = date.toString(),
        merchant = merchant,
        source = source.name,
        originalSmsId = originalSmsId,
        originalSmsBody = originalSmsBody,
        syncDate = syncDate,
        createdAt = createdAt
    )
}
