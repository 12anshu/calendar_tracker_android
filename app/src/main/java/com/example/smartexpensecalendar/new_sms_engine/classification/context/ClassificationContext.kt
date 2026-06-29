package com.example.smartexpensecalendar.new_sms_engine.classification.context

import com.example.smartexpensecalendar.new_sms_engine.classification.models.ClassificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Output of Classification phase.
 */
data class ClassificationContext(

    /**
     * Input from Qualification phase.
     */
    val qualification: QualificationContext,

    /**
     * Classification output.
     */
    val classification: ClassificationResult
)