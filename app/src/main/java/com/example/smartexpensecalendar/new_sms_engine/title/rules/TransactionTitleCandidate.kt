package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

data class TransactionTitleCandidate(

    val title: String,

    val confidence: Float,

    val evidence: List<TransactionTitleEvidence>
)