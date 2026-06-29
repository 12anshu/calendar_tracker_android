package com.example.smartexpensecalendar.new_sms_engine.classification.models

import com.example.smartexpensecalendar.new_sms_engine.common.enums.MessageType

/**
 * Result produced by Message Type Classification.
 */
data class MessageTypeResult(

    val type: MessageType,

    val confidence: Int,

    val score: Int,

    val evidence: List<String> = emptyList()
) {

    fun isTransaction() = type == MessageType.TRANSACTION

    fun isObligation() = type == MessageType.OBLIGATION

    fun isInformation() = type == MessageType.INFORMATION

    fun isUnknown() = type == MessageType.UNKNOWN
}