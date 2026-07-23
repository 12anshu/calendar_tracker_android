package com.example.smartexpensecalendar.new_sms_engine.classification.model

/**
 * Final result returned by MessageTypeClassifier.
 */
data class MessageTypeResult(

    val messageType: MessageType,

    val evidence: List<Evidence>

)