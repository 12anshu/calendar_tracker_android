package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.patterns

/**
 * Shared patterns used by Merchant Discovery.
 */
object MerchantDiscoveryPatterns {

    /**
     * Prepositions commonly preceding merchant names.
     */
    val PREPOSITIONS = listOf(
        " at ",
        " to ",
        " from ",
        " by ",
        " via ",
        "@"
    )

    /**
     * Characters indicating the end of a merchant window.
     */
    val WINDOW_TERMINATORS = charArrayOf(
        '\n',
        '.',
        ',',
        ':'
    )
}