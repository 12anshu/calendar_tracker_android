package com.example.smartexpensecalendar.sms_engine.detector

data class MessageTypeDetectionResult(

    val messageType: MessageType,

    val confidence: Int,

    val scores: Map<MessageType, Int>,

    val matchedKeywords: Map<MessageType, Set<String>>
)
