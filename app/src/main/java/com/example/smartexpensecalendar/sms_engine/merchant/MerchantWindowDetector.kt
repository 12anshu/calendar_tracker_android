package com.example.smartexpensecalendar.sms_engine.merchant

import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantWindow
import com.example.smartexpensecalendar.sms_engine.merchant.providers.MerchantWindowProvider
import com.example.smartexpensecalendar.sms_engine.merchant.providers.StandaloneWindowProvider

object MerchantWindowDetector {

    private val providers = listOf(

        StandaloneWindowProvider()

    )

    fun detect(
        text: String
    ): List<MerchantWindow> {

        return providers.flatMap { provider ->
            provider.detect(text)
        }
    }
}