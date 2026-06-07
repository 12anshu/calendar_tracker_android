package com.example.smartexpensecalendar.sms_engine.detector

data class MessageTypeResult(
    val messageType: MessageType,
    val confidence: Int,
    val score: Int,
//    val matchedKeywords: Set<String>
    val matchedPhrases: Set<String>
)
