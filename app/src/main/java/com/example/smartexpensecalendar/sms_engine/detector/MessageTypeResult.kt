package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.domain.model.MessageType

data class MessageTypeResult(
    val messageType: MessageType,
    val confidence: Int,
    val score: Int,
//    val matchedKeywords: Set<String>
    val matchedPhrases: Set<String>
)
