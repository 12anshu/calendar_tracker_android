package com.example.smartexpensecalendar.new_sms_engine.classification.common

import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Generic rule executed by a classifier.
 */
fun interface ClassificationRule<T> {

    fun evaluate(
        context: QualificationContext
    ): ClassificationRuleResult<T>
}