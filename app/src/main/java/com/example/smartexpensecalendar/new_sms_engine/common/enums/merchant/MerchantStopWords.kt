package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant

object MerchantStopWords {

    val WORDS = setOf(

        // Payment flow

        "via",
        "using",
        "through",

        // Time

        "on",
        "at",
        "by",

        // References

        "ref",
        "reference",
        "rrn",
        "utr",
        "txn",
        "txnid",

        // Banking

        "account",
        "a/c",
        "ac",
        "balance",
        "available",
        "avl",
        "limit",

        // Misc

        "if",
        "please",
        "call",
        "contact",
        "sms",
        "id"
    )
}