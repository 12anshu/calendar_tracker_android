package com.example.smartexpensecalendar.new_sms_engine.classification.messagetype

import com.example.smartexpensecalendar.new_sms_engine.classification.models.MessageTypeResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

interface MessageTypeClassifier {

    fun classify(
        context: QualificationContext
    ): MessageTypeResult
}