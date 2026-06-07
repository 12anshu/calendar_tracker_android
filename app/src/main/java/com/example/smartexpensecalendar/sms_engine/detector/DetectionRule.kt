package com.example.smartexpensecalendar.sms_engine.detector

data class DetectionRule(

    val name: String,

    val keywords: Set<String>,

    val score: Int
)
