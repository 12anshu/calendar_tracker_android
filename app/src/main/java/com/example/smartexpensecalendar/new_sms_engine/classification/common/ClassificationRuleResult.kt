package com.example.smartexpensecalendar.new_sms_engine.classification.common

/**
 * Internal result returned by a ClassificationRule.
 *
 * This model is consumed only by RuleBasedClassifier
 * and never exposed outside the Classification module.
 */
data class ClassificationRuleResult<T>(

    /**
     * Classification produced by this rule.
     */
    val classification: T,

    /**
     * Confidence score contributed by this rule.
     */
    val score: Int,

    /**
     * High-level reasoning.
     *
     * Example:
     * DEBIT_SIGNAL
     * CREDIT_SIGNAL
     */
    val evidence: List<String> = emptyList(),

    /**
     * Raw words, patterns or regex that matched.
     *
     * Example:
     * DEBITED
     * PAID
     * @YBL
     * ₹500
     */
    val matches: List<String> = emptyList()
)