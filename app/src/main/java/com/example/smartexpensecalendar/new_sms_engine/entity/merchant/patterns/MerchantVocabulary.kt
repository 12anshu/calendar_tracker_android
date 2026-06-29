package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.patterns

/**
 * Shared vocabulary used across Merchant Intelligence.
 */
object MerchantVocabulary {

    /**
     * Generic financial prefixes that cannot represent merchants.
     */
    val IGNORE_LINE_PREFIXES = setOf(
        "txn",
        "trxn",
        "transaction",
        "rs",
        "inr",
        "amt",
        "amount",
        "avl",
        "available",
        "balance",
        "call",
        "sms",
        "ref",
        "reference",
        "utr",
        "rrn",
        "ifsc",
        "not you",
        "dear",
        "hello",
        "hi"
    )
}