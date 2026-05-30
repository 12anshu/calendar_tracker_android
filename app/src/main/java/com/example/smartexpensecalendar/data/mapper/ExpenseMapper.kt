package com.example.smartexpensecalendar.data.mapper

import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = LocalDate.parse(date),
        merchant = merchant,
        source = ExpenseSource.valueOf(source),
        type = TransactionType.valueOf(type),
        status = TransactionStatus.valueOf(status),
        accountSuffix = accountSuffix,
        linkedId = linkedId,
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
        type = type.name,
        status = status.name,
        accountSuffix = accountSuffix,
        linkedId = linkedId,
        originalSmsId = originalSmsId,
        originalSmsBody = originalSmsBody,
        syncDate = syncDate,
        createdAt = createdAt
    )
}
