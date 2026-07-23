package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models

/**
 * Internal candidate used during extraction.
 */
data class MerchantCandidate(

    val merchant: String,

    val score: Int,

    val anchor: String? = null,

    val sourceSegment: String? = null
)