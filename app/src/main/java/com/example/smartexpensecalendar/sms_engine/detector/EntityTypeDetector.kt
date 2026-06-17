package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.domain.model.EntityType
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.PaymentMethod

object EntityTypeDetector {

    fun detect(
        merchant: String?,
        eventType: FinancialEventType,
        paymentMethod: PaymentMethod
    ): EntityType {
        val upperMerchant = merchant?.uppercase() ?: ""

        // 1. MEAL_CARD
        if (paymentMethod == PaymentMethod.MEAL_CARD ||
            upperMerchant.contains("MEAL CARD") ||
            upperMerchant.contains("PLUXEE") ||
            upperMerchant.contains("SODEXO") ||
            upperMerchant.contains("ZETA")
        ) {
            return EntityType.MEAL_CARD
        }

        // 2. ACCOUNT
        if (upperMerchant.contains("[A/C") ||
            upperMerchant.contains("ACCOUNT") ||
            upperMerchant.contains("BANK [A/C")
        ) {
            return EntityType.ACCOUNT
        }

        // 3. TRANSFER
        if (upperMerchant.contains("NEFT") ||
            upperMerchant.contains("IMPS") ||
            upperMerchant.contains("RTGS") ||
            upperMerchant.contains("TRANSFER") ||
            eventType == FinancialEventType.TRANSFER
        ) {
            return EntityType.TRANSFER
        }

        // 4. CARD_PAYMENT
        if (upperMerchant.contains("CARD PAYMENT") ||
            upperMerchant.contains("CREDIT CARD PAYMENT") ||
            eventType == FinancialEventType.CREDIT_CARD_PAYMENT
        ) {
            return EntityType.CARD_PAYMENT
        }

        // 5. SYSTEM
        if (upperMerchant.contains("STATEMENT") ||
            upperMerchant.contains("CASHBACK") ||
            upperMerchant.contains("REWARD") ||
            upperMerchant.contains("PAYMENT DUE") ||
            upperMerchant.contains("BILL GENERATED") ||
            merchant == null
        ) {
            return EntityType.SYSTEM
        }

        // DEFAULT
        return EntityType.MERCHANT
    }
}
