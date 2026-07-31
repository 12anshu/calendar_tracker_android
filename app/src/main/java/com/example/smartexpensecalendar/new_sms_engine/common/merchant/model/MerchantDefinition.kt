package com.example.smartexpensecalendar.new_sms_engine.common.merchant.model

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId

data class MerchantDefinition(

    val id: MerchantId,

    val category: CategoryId,

    val keywords: List<String>,

    val icon: String? = null,

    val website: String? = null,

    val tags: Set<String> = emptySet()
)