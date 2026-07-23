package com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model

/**
 * Optional token.
 *
 * Allows flexible sentence structures.
 *
 * Example:
 *
 * HAS BEEN DEBITED
 */
data class OptionalToken(

    val token: PatternToken

) : PatternToken