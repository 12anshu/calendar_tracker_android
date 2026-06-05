package com.example.smartexpensecalendar.sms.config

object SenderRegistry {

    val bankSenders = setOf(

        "HDFCBK",
        "ICICIB",
        "AXISBK",
        "SBIINB",
        "KOTAKB",
        "YESBNK",
        "IDFCFB",
        "RBLBNK",
        "FEDBNK"
    )

    val cardSenders = setOf(

        "SBICRD",
        "HDFCBK",
        "ICICIB"
    )

    val upiSenders = setOf(

        "PAYTM",
        "PHONEPE",
        "GPAY",
        "GOOGLEPAY"
    )
}