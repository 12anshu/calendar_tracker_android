package com.example.smartexpensecalendar.new_sms_engine.classification.common

import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Base implementation for all rule-based classifiers.
 */
abstract class RuleBasedClassifier<T> {

    protected abstract val rules: List<ClassificationRule<T>>

    /**
     * Executes all rules.
     */
    protected fun executeRules(
        context: QualificationContext
    ): List<ClassificationRuleResult<T>> {

        return rules.map {
            it.evaluate(context)
        }
    }

    /**
     * Returns the highest scoring rule.
     */
    protected fun bestResult(
        results: List<ClassificationRuleResult<T>>
    ): ClassificationRuleResult<T> {

        return results
            .sortedByDescending { it.score }
            .first()
    }

    /**
     * Collects evidence from all executed rules.
     */
    protected fun collectEvidence(
        results: List<ClassificationRuleResult<T>>
    ): List<String> {

        return results
            .flatMap { it.evidence }
            .distinct()
    }

    protected fun collectMatches(
        results: List<ClassificationRuleResult<T>>
    ): List<String> {

        return results
            .flatMap { it.matches }
            .distinct()
    }
}