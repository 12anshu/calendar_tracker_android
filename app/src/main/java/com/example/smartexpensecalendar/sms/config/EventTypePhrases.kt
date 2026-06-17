package com.example.smartexpensecalendar.sms.config

object EventTypePhrases {

    val incomePhrases = setOf(
        "SALARY CREDITED",
        "CASHBACK CREDITED",
        "REWARD CREDITED",
        "INTEREST CREDITED",
        "REFUND CREDITED",
        "PAYMENT RECEIVED",
        "AMOUNT RECEIVED",
        "FUNDS RECEIVED",
        "MONEY RECEIVED"
    )

    val refundPhrases = setOf(
        "REFUND INITIATED",
        "REFUND PROCESSED",
        "REFUND COMPLETED",
        "REFUND SUCCESSFUL",
        "REFUND CREDITED",
        "AMOUNT REVERSED",
        "REVERSAL PROCESSED",
        "CHARGE REVERSAL"
    )

    val transferPhrases = setOf(
        "FUND TRANSFER",
        "MONEY TRANSFER",
        "ACCOUNT TO ACCOUNT",
        "BENEFICIARY TRANSFER",
        "TRANSFERRED TO",
        "TRANSFERRED FROM",
        "IMPS TRANSFER",
        "NEFT TRANSFER",
        "RTGS TRANSFER",
        "UPI TRANSFER"
    )

    val creditCardPaymentPhrases = setOf(
        "CREDIT CARD PAYMENT",
        "CARD BILL PAYMENT",
        "PAID TOWARDS YOUR CREDIT CARD",
        "PAYMENT RECEIVED FOR YOUR CARD",
        "CREDIT CARD DUES PAID",
        "PAYMENT RECEIVED ON",
        "RECEIVED TOWARDS YOUR CREDIT CARD",
        "PAYMENT OF {CUR}",
        "PAYMENT OF {CUR} IS RECEIVED",
        "PAYMENT HAS BEEN RECEIVED",
        "PAYMENT RECEIVED TOWARDS",
        "RECEIVED ON CREDIT CARD",
        "PAYMENT RECEIVED ON CREDIT CARD",
        "IS RECEIVED",
        "IS RECIEVED"
    )

    val creditCardSpendPhrases = setOf(
        "SPENT ON CARD",
        "CARD PURCHASE",
        "PURCHASE ON CARD",
        "CARD TRANSACTION",
        "CARD USED",
        "POS TRANSACTION",
        "SWIPED AT",
        "SPENT {CUR} ON CARD"
    )

    val emiPhrases = setOf(
        "EMI PAYMENT",
        "EMI DEBITED",
        "LOAN INSTALLMENT",
        "INSTALLMENT DEDUCTED",
        "EMI DEDUCTED",
        "TOWARDS LOAN",
        "LOAN REPAYMENT",
        "EMI PROCESSED",
        "EMI OF {CUR}"
    )

    val emiConversionPhrases = setOf(
        "CONVERTED TO EMI",
        "CONVERTED INTO EMI",
        "EMI CONVERSION",
        "EMI CONVERSION SUCCESSFUL",
        "PURCHASE CONVERTED",
        "TXN CONVERTED TO EMI"
    )

    val investmentPhrases = setOf(
        "MUTUAL FUND PURCHASE",
        "SIP INSTALLMENT",
        "SIP PAYMENT",
        "FD BOOKED",
        "RD INSTALLMENT",
        "NPS CONTRIBUTION",
        "DEMAT PURCHASE",
        "STOCK PURCHASE"
    )

    val cashWithdrawalPhrases = setOf(
        "CASH WITHDRAWAL",
        "ATM WITHDRAWAL",
        "CASH WITHDRAWN",
        "WITHDRAWN AT ATM"
    )

    val cashDepositPhrases = setOf(
        "CASH DEPOSIT",
        "CASH DEPOSITED",
        "DEPOSITED AT BRANCH",
        "CASH DEPOSITED IN ACCOUNT"
    )

    val mealCardPhrases = setOf(
        "MEAL CARD",
        "SODEXO",
        "ZETA",
        "TICKET RESTAURANT",
        "PLUXEE",
        "MEAL VOUCHER",
        "FOOD CARD",
        "BENEFIT CARD",
        "EDENRED",
        "SWILE",
        "RESTO FLASH",
        "CHEQUE DEJEUNER",
        "UPDEJEUNER",
        "GLOVO BUSINESS"
    )

}
