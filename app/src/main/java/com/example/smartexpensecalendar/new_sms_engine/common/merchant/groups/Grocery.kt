package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.groceries
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val Grocery = listOf(

    groceries(
        MerchantId.DMART,
        "DMART",
        "D MART"
    ),

    groceries(
        MerchantId.RELIANCE_SMART,
        "RELIANCE SMART",
        "SMART BAZAAR"
    ),

    groceries(
        MerchantId.RELIANCE_FRESH,
        "RELIANCE FRESH"
    ),

    groceries(
        MerchantId.SPENCERS,
        "SPENCERS",
        "SPENCER'S"
    ),

    groceries(
        MerchantId.MORE,
        "MORE"
    ),

    groceries(
        MerchantId.NATURES_BASKET,
        "NATURES BASKET",
        "NATURE'S BASKET"
    ),

    groceries(
        MerchantId.METRO,
        "METRO CASH",
        "METRO CASH & CARRY"
    ),

    groceries(
        MerchantId.JIOMART,
        "JIOMART",
        "JIO MART"
    )
)