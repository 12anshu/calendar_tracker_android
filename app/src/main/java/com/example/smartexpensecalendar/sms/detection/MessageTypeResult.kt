package com.example.smartexpensecalendar.sms.detection

data class MessageTypeResult(
    val messageType: MessageType,
    val confidence: Int,
    val score: Int,
    val matchedKeywords: Set<String>
)