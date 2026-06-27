package com.example.smartexpensecalendar.sms_engine.merchant.model

data class MerchantCandidate(

    val merchant: String,

    val evidence: MutableList<MerchantEvidence> =
        mutableListOf()

) {

    val totalScore: Int
        get() = evidence.sumOf { it.score }

    val confidence: Int
        get() = when {
            totalScore >= 250 -> 100
            totalScore >= 200 -> 90
            totalScore >= 150 -> 80
            totalScore >= 100 -> 70
            totalScore >= 50 -> 60
            else -> 0
        }
}