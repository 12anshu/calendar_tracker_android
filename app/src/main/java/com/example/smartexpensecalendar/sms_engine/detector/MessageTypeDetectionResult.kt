package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.domain.model.TransactionDirection

data class MessageTypeDetectionResult(
    val messageType: MessageType,
    val confidence: Int,
    val scores: Map<MessageType, Int> = emptyMap(),
    val matchedKeywords: Map<MessageType, Set<String>> = emptyMap(),
    val detectedDirection: TransactionDirection = TransactionDirection.UNKNOWN
)
