package com.example.smartexpensecalendar.sms.merchant

import com.example.smartexpensecalendar.sms.config.MerchantRegistry

object MerchantMatcher {

    fun match(
        merchant: String?
    ): MerchantMatchResult {

        val cleaned =
            MerchantCleanupEngine.cleanup(
                merchant
            )

        if (cleaned.isBlank()) {
            return MerchantMatchResult(
                canonicalName = null,
                confidence = 0
            )
        }

        MerchantRegistry.merchants.forEach { definition ->

            definition.aliases.forEach { alias ->

                when {

                    cleaned == alias -> {

                        return MerchantMatchResult(
                            canonicalName =
                                definition.canonicalName,

                            confidence = 100,

                            matchedAlias = alias
                        )
                    }

                    cleaned.split(" ")
                        .contains(alias) -> {

                        return MerchantMatchResult(
                            canonicalName =
                                definition.canonicalName,

                            confidence = 95,

                            matchedAlias = alias
                        )
                    }

                    cleaned.contains(alias) -> {

                        return MerchantMatchResult(
                            canonicalName =
                                definition.canonicalName,

                            confidence = 90,

                            matchedAlias = alias
                        )
                    }

                    cleaned.startsWith(alias) -> {

                        return MerchantMatchResult(
                            canonicalName =
                                definition.canonicalName,

                            confidence = 85,

                            matchedAlias = alias
                        )
                    }

                    alias.startsWith(cleaned) -> {

                        return MerchantMatchResult(
                            canonicalName =
                                definition.canonicalName,

                            confidence = 80,

                            matchedAlias = alias
                        )
                    }
                }
            }
        }

        return MerchantMatchResult(
            canonicalName = null,
            confidence = 0
        )
    }
}