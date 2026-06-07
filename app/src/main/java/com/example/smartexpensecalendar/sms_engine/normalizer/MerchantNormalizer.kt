package com.example.smartexpensecalendar.sms_engine.normalizer

object MerchantNormalizer {

    fun normalize(
        merchant: String?
    ): String? {

        if (merchant.isNullOrBlank()) {
            return null
        }

        val value = merchant.uppercase()

        return when {

            value.contains("ZEPTO") ->
                "Zepto"

            value.contains("SWIGGY") ||
                    value.contains("BUNDL") ->
                "Swiggy"

            value.contains("ZOMATO") ->
                "Zomato"

            value.contains("FLIPKART") ->
                "Flipkart"

            value.contains("AMAZON") ||
                    value.contains("AMZN") ->
                "Amazon"

            value.contains("BLINKIT") ->
                "Blinkit"

            value.contains("UBER") ->
                "Uber"

            value.contains("OLA") ->
                "Ola"

            value.contains("NETFLIX") ->
                "Netflix"

            value.contains("SPOTIFY") ->
                "Spotify"

            value.contains("GOOGLE PLAY") ->
                "Google Play"

            value.contains("BHARATPE") ->
                "BharatPe"

            value.contains("PHONEPE") ||
                    value.contains("PME@YBL") ->
                "PhonePe"

            value.contains("BIGBASKET") ||
                    value.contains("BBDAILY") ->
                "BigBasket"

            value.contains("DUNZO") ->
                "Dunzo"

            value.contains("JIO") ||
                    value.contains("RELIANCE") ->
                "Reliance Jio"

            value.contains("AIRTEL") ->
                "Airtel"

            value.contains("TATA 1MG") ||
                    value.contains("1MG") ->
                "1mg"

            value.contains("PAYTM") ->
                "Paytm"

            value.contains("KICK TONIC") ->
                "Kick Tonic"

            value.contains("PICKNPAY") ->
                "Pick N Pay"

            value.contains("ETERNALLIM") ||
                    value.contains("ETERNAL LIM") ->
                "Zomato"

            value.contains("CHEQ") ->
                "CheQ"

            value.contains("VYAPAR") ->
                "Vyapar"

            value.contains("STARBUCKS") ||
                    value.contains("TATA STARBUCKS") ->
                "Starbucks"

            value.contains("MCDONALD") ->
                "McDonald's"

            value.contains("KFC") ->
                "KFC"

            value.contains("DOMINO") ->
                "Domino's"

            else ->
                merchant.trim()
        }
    }
}
