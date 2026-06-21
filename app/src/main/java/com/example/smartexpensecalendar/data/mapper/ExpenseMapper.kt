package com.example.smartexpensecalendar.data.mapper

import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.ExpenseSource
import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.PaymentMethod
import com.example.smartexpensecalendar.domain.model.EntityType
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = LocalDate.parse(date),
        merchant = merchant,
        financialEventType =
            FinancialEventType.valueOf(
                financialEventType
            ),
        paymentMethod =
            PaymentMethod.valueOf(
                paymentMethod
            ),
        confidence = confidence,
        source = ExpenseSource.valueOf(source),
        type = TransactionType.valueOf(type),
        status = TransactionStatus.valueOf(status),
        accountSuffix = accountSuffix,
        accountName = accountName,
        transactionTime = transactionTime,
        senderId = senderId,
        quality = quality,
        entityType = EntityType.valueOf(entityType),
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
        financialEventType =
            financialEventType.name,
        paymentMethod =
            paymentMethod.name,
        confidence =
            confidence,
        source = source.name,
        type = type.name,
        status = status.name,
        accountSuffix = accountSuffix,
        accountName = accountName,
        transactionTime = transactionTime,
        senderId = senderId,
        quality = quality,
        entityType = entityType.name,
        linkedId = linkedId,
        originalSmsId = originalSmsId,
        originalSmsBody = originalSmsBody,
        syncDate = syncDate,
        createdAt = createdAt
    )
}
