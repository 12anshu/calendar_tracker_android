package com.example.smartexpensecalendar.new_sms_engine.common.merchant.normalization

interface MerchantNormalizer {

    fun normalize(
        merchant: String?
    ): String?
}