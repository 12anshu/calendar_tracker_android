package com.example.smartexpensecalendar.sms.detection

data class DetectionRule(

    val name: String,

    val keywords: Set<String>,

    val score: Int
)