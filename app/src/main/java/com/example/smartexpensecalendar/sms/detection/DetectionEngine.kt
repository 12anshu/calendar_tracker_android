package com.example.smartexpensecalendar.sms.detection

object DetectionEngine {

    fun detect(
        sms: String
    ): DetectionResult {

        val text = sms.uppercase()

        val detectedFields =
            mutableSetOf<DetectedField>()

        if (hasAmount(text)) {
            detectedFields.add(
                DetectedField.AMOUNT
            )
        }

        if (hasAccount(text)) {
            detectedFields.add(
                DetectedField.ACCOUNT
            )
        }

        if (hasCard(text)) {
            detectedFields.add(
                DetectedField.CARD
            )
        }

        if (hasPaymentMethod(text)) {
            detectedFields.add(
                DetectedField.PAYMENT_METHOD
            )
        }

        val transactionClass =
            detectTransactionClass(text)

        val isFinancial =
            transactionClass !=
                    TransactionClass.UNKNOWN

        val confidence =
            calculateConfidence(
                isFinancial,
                detectedFields.size
            )

        return DetectionResult(
            isFinancial =
                isFinancial,

            transactionClass =
                transactionClass,

            confidence =
                confidence,

            detectedFields =
                detectedFields
        )
    }

    private fun hasAmount(
        text: String
    ): Boolean {

        return Regex(
            "(RS\\.?|INR)\\s?\\d+"
        ).containsMatchIn(text)
    }

    private fun hasAccount(
        text: String
    ): Boolean {

        return text.contains("A/C") ||
                text.contains("ACCOUNT")
    }

    private fun hasCard(
        text: String
    ): Boolean {

        return text.contains("CARD")
    }

    private fun hasPaymentMethod(
        text: String
    ): Boolean {

        return listOf(
            "UPI",
            "IMPS",
            "NEFT",
            "RTGS"
        ).any {
            text.contains(it)
        }
    }

    private fun detectTransactionClass(
        text: String
    ): TransactionClass {

        return when {

            text.contains("REFUND") ->
                TransactionClass.REFUND

            text.contains("EMI") ->
                TransactionClass.EMI

            text.contains("PAYMENT RECEIVED") &&
                    text.contains("CREDIT CARD") ->
                TransactionClass.CARD_PAYMENT

            text.contains("NON-MAINTENANCE FEE") ||
                    text.contains("CHARGE") ->
                TransactionClass.BANK_CHARGE

            text.contains("WITHDRAWN") ->
                TransactionClass.CASH_WITHDRAWAL

            text.contains("AUTOPAY") ->
                TransactionClass.SUBSCRIPTION

            text.contains("MANDATE") ->
                TransactionClass.MANDATE

            text.contains("TRANSFERRED") ||
                    text.contains("IMPS") ||
                    text.contains("NEFT") ||
                    text.contains("RTGS") ->
                TransactionClass.TRANSFER

            text.contains("SPENT") ->
                TransactionClass.CARD_SPEND

            text.contains("DEBITED") ->
                TransactionClass.ACCOUNT_DEBIT

            text.contains("CREDITED") ->
                TransactionClass.ACCOUNT_CREDIT

            else ->
                TransactionClass.UNKNOWN
        }
    }

    private fun calculateConfidence(
        isFinancial: Boolean,
        fieldCount: Int
    ): Int {

        if (!isFinancial) {
            return 0
        }

        return minOf(
            100,
            50 + (fieldCount * 10)
        )
    }
}