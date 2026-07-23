package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models

data class MerchantResult(

    /**
     * Raw merchant extracted from SMS.
     */
    val merchant: String?,

    /**
     * Extraction confidence.
     */
    val confidence: Float,

    val anchor: String? = null,

    val sourceSegment: String? = null
)