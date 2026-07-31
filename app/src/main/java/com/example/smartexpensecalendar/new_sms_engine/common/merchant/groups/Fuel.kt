package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.fuel
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val Fuel = listOf(

    fuel(
        MerchantId.INDIAN_OIL,
        "INDIAN OIL",
        "INDIANOIL",
        "IOCL"
    ),

    fuel(
        MerchantId.BHARAT_PETROLEUM,
        "BHARAT PETROLEUM",
        "BPCL"
    ),

    fuel(
        MerchantId.HINDUSTAN_PETROLEUM,
        "HINDUSTAN PETROLEUM",
        "HPCL"
    ),

    fuel(
        MerchantId.SHELL,
        "SHELL"
    ),

    fuel(
        MerchantId.NAYARA,
        "NAYARA",
        "ESSAR"
    )
)