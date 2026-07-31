package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.travel
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val Mobility = listOf(

    travel(
        MerchantId.UBER,
        "UBER"
    ),

    travel(
        MerchantId.OLA,
        "OLA"
    ),

    travel(
        MerchantId.RAPIDO,
        "RAPIDO"
    ),

    travel(
        MerchantId.BLUSMART,
        "BLUSMART"
    ),

    travel(
        MerchantId.YULU,
        "YULU"
    ),

    travel(
        MerchantId.NAMMA_YATRI,
        "NAMMA YATRI", "NAMMAYATRI"
    ),
)