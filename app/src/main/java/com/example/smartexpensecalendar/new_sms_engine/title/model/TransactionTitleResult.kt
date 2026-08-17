package com.example.smartexpensecalendar.new_sms_engine.title.model

import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

data class TransactionTitleResult(

    val title: String,

    val confidence: Float,

    val evidence: List<TransactionTitleEvidence>
)