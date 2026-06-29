package com.example.smartexpensecalendar.new_sms_engine.classification.mode

import com.example.smartexpensecalendar.new_sms_engine.classification.models.TransactionModeResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

interface TransactionModeClassifier {

    fun classify(
        context: QualificationContext
    ): TransactionModeResult
}