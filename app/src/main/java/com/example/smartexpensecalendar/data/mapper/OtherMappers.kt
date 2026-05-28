package com.example.smartexpensecalendar.data.mapper

import com.example.smartexpensecalendar.data.local.entity.MerchantMappingEntity
import com.example.smartexpensecalendar.data.local.entity.SMSLogEntity
import com.example.smartexpensecalendar.domain.model.MerchantMapping
import com.example.smartexpensecalendar.domain.model.ProcessingStatus
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog

fun MerchantMappingEntity.toDomain() = MerchantMapping(merchantKeyword, category)
fun MerchantMapping.toEntity() = MerchantMappingEntity(merchantKeyword, category)

fun SMSLogEntity.toDomain() = SMSProcessingLog(
    smsId, sender, body, date, ProcessingStatus.valueOf(status), failureReason, parsedAmount, parsedMerchant
)
fun SMSProcessingLog.toEntity() = SMSLogEntity(
    smsId, sender, body, date, status.name, failureReason, parsedAmount, parsedMerchant
)
