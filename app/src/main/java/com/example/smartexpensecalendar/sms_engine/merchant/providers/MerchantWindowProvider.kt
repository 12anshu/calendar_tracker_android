package com.example.smartexpensecalendar.sms_engine.merchant.providers

import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantWindow
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantWindowSource
import com.example.smartexpensecalendar.sms_engine.model.ExtractionContext

interface MerchantWindowProvider {

    val source: MerchantWindowSource

    fun detect(
        text: String,
        context: ExtractionContext
    ): List<MerchantWindow>
}