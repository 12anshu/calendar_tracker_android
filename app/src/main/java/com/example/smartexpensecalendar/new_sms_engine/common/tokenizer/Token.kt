package com.example.smartexpensecalendar.new_sms_engine.common.tokenizer

data class Token(

    val text: String,

    val index: Int,

    val lineIndex: Int,

    val categories: Set<TokenCategory>

) {

    fun has(category: TokenCategory): Boolean {
        return category in categories
    }

    fun hasCategory(
        tokens: List<Token>,
        category: TokenCategory
    ): Boolean {
        return tokens.any { it.has(category) }
    }

    fun hasAnyCategory(
        tokens: List<Token>,
        categories: Set<TokenCategory>
    ): Boolean {
        return tokens.any { it.hasAny(categories) }
    }

    fun hasAny(categories: Set<TokenCategory>): Boolean {
        return this.categories.any { it in categories }
    }

    fun withCategory(category: TokenCategory): Token {
        return copy(categories = this.categories + category)
    }

    fun withCategories(categories: Set<TokenCategory>): Token {
        return copy(categories = this.categories + categories)
    }
}