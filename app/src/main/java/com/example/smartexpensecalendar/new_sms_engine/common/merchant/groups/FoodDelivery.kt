package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.food
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val FoodDelivery = listOf(

    food(
        MerchantId.SWIGGY,
        "SWIGGY",
        "SWIGGY LIMITED"
    ),

    food(
        MerchantId.ZOMATO,
        "ZOMATO"
    ),

    food(
        MerchantId.EATSURE,
        "EATSURE",
        "REBEL FOODS"
    ),

    food(
        MerchantId.DOMINOS,
        "DOMINOS",
        "DOMINO'S PIZZA"
    )
)