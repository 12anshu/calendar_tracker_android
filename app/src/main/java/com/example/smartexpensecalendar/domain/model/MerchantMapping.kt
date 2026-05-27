package com.example.smartexpensecalendar.domain.model

/**
 * Maps a merchant name extracted from SMS to a category.
 * This can be pre-seeded and also updated when the user changes a category for a merchant.
 */
data class MerchantMapping(
    val merchantKeyword: String,
    val category: String
)
