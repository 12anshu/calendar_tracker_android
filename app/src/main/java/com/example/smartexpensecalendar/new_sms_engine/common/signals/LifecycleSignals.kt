package com.example.smartexpensecalendar.new_sms_engine.common.signals

object LifecycleSignals {

    /**
     * EMI conversion request / offer language.
     *
     * Examples:
     * - convert your transaction to EMI
     * - convert this txn to EMI
     * - you can convert the transaction to EMI
     */
    val EMI_CONVERSION_REQUEST_SIGNALS = setOf(
        "CONVERT",
        "CONVERTING",
        "CONVERTIBLE"
    )

    /**
     * EMI conversion completed language.
     *
     * Examples:
     * - transaction converted to EMI
     * - transaction has been converted into EMI
     * - transaction was converted to EMI
     */
    val EMI_CONVERSION_COMPLETED_SIGNALS = setOf(
        "CONVERTED"
    )

    /**
     * Noun-form conversion language is intentionally not classified
     * as completed yet because "conversion into EMI" can describe
     * either an offer/process or a completed lifecycle event.
     */
    val EMI_CONVERSION_NOUN_SIGNALS = setOf(
        "CONVERSION"
    )
}