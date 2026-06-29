package com.example.smartexpensecalendar.new_sms_engine.qualification.context

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationResult

/**
 * Output of Qualification phase.
 */
data class QualificationContext(

    val input: QualificationInput,

    val qualification: QualificationResult
)