package com.example.smartexpensecalendar.sms_engine.merchant

import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantCandidate

object MerchantResolver {

    fun resolve(
        candidates: List<MerchantCandidate>
    ): MerchantCandidate? {

        if (candidates.isEmpty()) {
            return null
        }

        return candidates.maxByOrNull {
            it.totalScore
        }
    }
}