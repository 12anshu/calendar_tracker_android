package com.example.smartexpensecalendar.new_sms_engine.common.merchant.groups

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantId
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.upi

internal val PaymentPlatforms = listOf(
    upi(MerchantId.PHONEPE, "phonepe", "ybl", "ibl", "PHONEPEBUSINESS"),
    upi(MerchantId.PAYTM, "paytm", "PAYTMQR", "ONE97"),
    upi(MerchantId.BHIM, "bhim", "upi"),
    upi(MerchantId.AMAZON_PAY, "amazonpay", "apl"),
    upi(MerchantId.MOBIKWIK, "mobikwik", "ikwik"),
    upi(MerchantId.FREECHARGE, "freecharge", "idfcnetc"),
    upi(MerchantId.BHARATPE, "bharatpe"),
    upi(MerchantId.CRED, "cred", "gemini"),
    upi(MerchantId.SLICE, "slice", "slc"),
    upi(MerchantId.GPAY, "gpay", "googlepay", "okaxis", "okicici", "oksbi", "okhdfcbank")
)