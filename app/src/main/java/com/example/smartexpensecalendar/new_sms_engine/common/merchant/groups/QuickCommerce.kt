package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.groceries
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val QuickCommerce = listOf(

    groceries(
        MerchantId.ZEPTO,
        "ZEPTO"
    ),

    groceries(
        MerchantId.BLINKIT,
        "BLINKIT",
        "GROFERS"
    ),

    groceries(
        MerchantId.BIGBASKET,
        "BIGBASKET",
        "BIG BASKET",
        "BB NOW"
    ),

    groceries(
        MerchantId.INSTAMART,
        "INSTAMART",
        "SWIGGY"
    )
)