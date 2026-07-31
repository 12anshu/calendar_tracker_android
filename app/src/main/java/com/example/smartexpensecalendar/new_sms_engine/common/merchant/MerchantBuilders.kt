package com.example.smartexpensecalendar.new_sms_engine.common.merchant

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantDefinition
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal fun groceries(
    id: MerchantId,
    vararg keywords: String
) = MerchantDefinition(
    id = id,
    category = CategoryId.GROCERIES,
    keywords = keywords.toList()
)

internal fun food(
    id: MerchantId,
    vararg keywords: String
) = MerchantDefinition(
    id = id,
    category = CategoryId.FOOD,
    keywords = keywords.toList()
)

internal fun shopping(
    id: MerchantId,
    vararg keywords: String
) = MerchantDefinition(
    id = id,
    category = CategoryId.SHOPPING,
    keywords = keywords.toList()
)

internal fun fuel(
    id: MerchantId,
    vararg keywords: String
) = MerchantDefinition(
    id = id,
    category = CategoryId.FUEL,
    keywords = keywords.toList()
)

internal fun travel(
    id: MerchantId,
    vararg keywords: String
) = MerchantDefinition(
    id = id,
    category = CategoryId.TRAVEL,
    keywords = keywords.toList()
)