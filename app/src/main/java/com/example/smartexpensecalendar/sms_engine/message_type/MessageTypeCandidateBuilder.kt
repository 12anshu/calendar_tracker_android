package com.example.smartexpensecalendar.sms_engine.message_type

import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.sms_engine.model.Candidate

object MessageTypeCandidateBuilder {

    fun build(
        smsText: String
    ): List<Candidate<MessageType>> {

        return listOf(
            Candidate(MessageType.TRANSACTION),
            Candidate(MessageType.OBLIGATION),
            Candidate(MessageType.INFORMATION)
        )
    }
}