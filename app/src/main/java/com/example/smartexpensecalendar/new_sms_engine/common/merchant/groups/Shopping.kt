package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.shopping
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId

internal val Shopping = listOf(

    shopping(
        MerchantId.AMAZON,
        "AMAZON",
        "AMAZON PAY"
    ),

    shopping(
        MerchantId.FLIPKART,
        "FLIPKART",
        "FKART"
    ),

    shopping(
        MerchantId.MYNTRA,
        "MYNTRA"
    ),

    shopping(
        MerchantId.AJIO,
        "AJIO"
    ),

    shopping(
        MerchantId.NYKAA,
        "NYKAA"
    ),

    shopping(
        MerchantId.MEESHO,
        "MEESHO"
    ),

    shopping(
        MerchantId.TATA_CLIQ,
        "TATA CLIQ",
        "TATACLIQ"
    ),

    shopping(
        MerchantId.FIRSTCRY,
        "FIRSTCRY",
        "FIRST CRY"
    ),

    shopping(
        MerchantId.SNAPDEAL,
        "SNAPDEAL"
    ),

    shopping(
        MerchantId.LENSKART,
        "LENSKART"
    )
)