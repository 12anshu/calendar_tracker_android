package com.example.smartexpensecalendar.new_sms_engine.classification.event

import com.example.smartexpensecalendar.new_sms_engine.classification.models.FinancialEventResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

interface FinancialEventClassifier {

    fun classify(
        context: QualificationContext
    ): FinancialEventResult
}