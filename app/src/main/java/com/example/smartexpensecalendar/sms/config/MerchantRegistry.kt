package com.example.smartexpensecalendar.sms.config

data class MerchantDefinition(

    val canonicalName: String,

    val aliases: Set<String>
)

object MerchantRegistry {

    val merchants = listOf(

        MerchantDefinition(
            canonicalName = "Swiggy",
            aliases = setOf(
                "SWIGGY",
                "BUNDL",
                "INSTAMART",
                "SWIGGY FOOD",
                "SWIGGY IN"
            )
        ),

        MerchantDefinition(
            canonicalName = "Zomato",
            aliases = setOf(
                "ZOMATO",
                "PYU ZOMATO"
            )
        ),

        MerchantDefinition(
            canonicalName = "Amazon",
            aliases = setOf(
                "AMAZON",
                "AMZN",
                "AMAZON PAY"
            )
        ),

        MerchantDefinition(
            canonicalName = "BigBasket",
            aliases = setOf(
                "BIGBASKET",
                "BBDAILY",
                "WWWBIGBASKETCOM"
            )
        ),

        MerchantDefinition(
            canonicalName = "Blinkit",
            aliases = setOf(
                "BLINKIT",
                "GROFERS"
            )
        ),

        MerchantDefinition(
            canonicalName = "Zepto",
            aliases = setOf(
                "ZEPTO",
                "ZEPTONOW",
                "ZEPTO MARKETPLACE"
            )
        ),

        MerchantDefinition(
            canonicalName = "Cred",
            aliases = setOf(
                "CRED",
                "CREDCLUB",
                "RAZPCREDCLUB"
            )
        )
    )
}