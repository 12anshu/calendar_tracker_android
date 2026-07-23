package com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model

import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

/**
 * Matches one or more semantic token categories.
 */
data class CategoryToken(

    val categories: Set<TokenCategory>

) : PatternToken {

    constructor(category: TokenCategory) : this(setOf(category))
}