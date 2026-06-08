package com.example.smartexpensecalendar.sms.config

object MessageTypePhrases {

    // --- STAGE 1: OBLIGATION (Reminders / Future Actions) ---
    // High Priority: Checked first to stop reminders from appearing as transactions.
    val obligationPhrases = setOf(
        "IS DUE ON",
        "OVERDUE ON",
        "MINIMUM AMOUNT DUE",
        "PAYMENT DUE DATE",
        "PAY BEFORE",
        "TOTAL AMOUNT DUE",
        "REPAYMENT OF",
        "MANDATE CREATED",
        "AUTOPAY REGISTRATION",
        "WILL BE DEBITED ON",
        "WILL BE AUTO DEBITED",
        "PENDING ON",
        "DUE BY",
        "TOTAL DUE",
        "MIN. DUE",
        "MINIMUM DUE",
        "IS DUE FOR PAYMENT",
        "MAINTAIN ADEQUATE BALANCE",
        "ENSURE SUFFICIENT BALANCE",
        "SCHEDULED ON",
        "REQUESTED A PAYMENT",
        "DUE DATE",
        "ईएमआई की देय तिथि", // Hindi: EMI Due Date
        "धनराशि बनाए रखें"    // Hindi: Maintain balance
    )

    // --- STAGE 2: TRANSACTION (Actual Money Movement) ---
    // High Priority: Checked second. This overrides "Balance" phrases if both exist.
    
    // Confirmed OUTGOING (Expenses/Spends)
    val transactionDebitPhrases = setOf(
        "SPENT RS",
        "SPENT INR",
        "SPENT ON",
        "SPENT USING",
        "SPENT FROM",
        "SPENT AT",
        "PAID TO",
        "PAID VIA",
        "PAID USING",
        "DEBITED FROM",
        "WAS DEBITED",
        "IS DEBITED",
        "HAS BEEN DEBITED",
        "SENT FROM YOUR ACCOUNT",
        "DEDUCTED FROM",
        "AMT DEDUCTED",
        "WITHDRAWN VIA",
        "WITHDRAWN FROM",
        "WITHDRAWAL FROM",
        "WITHDRAWAL AT",
        "CASH WITHDRAWAL",
        "PURCHASE MADE",
        "PURCHASED AT",
        "SWIPED ON",
        "CHARGED ON",
        "CHARGE OF RS",
        "AUTOPAY SUCCESSFUL",
        "E-MANDATE SUCCESS",
        "TXN RS",
        "TXN INR",
        "TRANSACTION OF RS",
        "MONEY SENT",
        "SENT TO",
        "SENT FROM",
        "TRANSFERRED TO",
        "TRANSFERRED FROM YOUR A/C",
        "TRANSFERRED INR",
        "TRANSFER TO",
        "WITHOUT OTP", 
        "WITHOUT PIN",
        "BILL PAYMENT SUCCESSFUL",
        "PAYMENT SUCCESSFUL",
        "RECHARGE SUCCESSFUL",
        "STANDING INSTRUCTION EXECUTED",
        "SUCCESSFULLY PAID",
        "PAID RS",
        "PAID INR",
        "MONEY DEBITED",
        "DEBITED FOR RS",
        "ನಿಕಾಲೇ ಗಯೇ", // Hindi: Withdrawn (Romanized)
        "निकाले गए"    // Hindi: Withdrawn
    )

    // Confirmed INCOMING (Income/Refunds/Cashback)
    val transactionCreditPhrases = setOf(
        "CREDITED TO",
        "IS CREDITED WITH",
        "AMT CREDITED",
        "AMOUNT CREDITED",
        "SUCCESSFULLY DEPOSITED",
        "DEPOSITED IN",
        "DEPOSITED TO",
        "AMT DEPOSITED",
        "AMOUNT DEPOSITED IN",
        "RECEIVED IN",
        "RECEIVED ON",
        "FUNDS RECEIVED",
        "MONEY RECEIVED",
        "CASHBACK CREDITED",
        "CASHBACK OF",
        "REFUND PROCESSED",
        "REFUND CREDITED",
        "REWARD CREDITED",
        "PAYMENT RECEIVED",
        "PAYMENT OF", 
        "FRESH FUNDS",
        "GOT FRESH FUNDS",
        "TRANSFERRED TO YOUR ACCOUNT",
        "TRANSFER FROM",
        "TRANSFERRED FROM",
        "ADDED TO YOUR",
        "SUCCESSFULLY CREDITED",
        "MONEY CREDITED",
        "RECEIVED RS",
        "RECEIVED INR",
        "CREDITED FOR RS",
        "REVERSAL OF",
        "LOADED IN WALLET",
        "ADDED IN WALLET",
        "ಜಮಾ ಕಿಯಾ ಗಯಾ", // Hindi: Deposited (Romanized)
        "जमा किया गया",   // Hindi: Deposited
        "ईएमआई प्राप्त हुई" // Hindi: EMI Received
    )

    // --- STAGE 3: INFORMATION (Status / Non-Financial Alerts) ---
    // Low Priority: Only matched if no Transaction or Obligation was found.
    val informationPhrases = setOf(
        "AVAILABLE BALANCE",
        "CURRENT BALANCE",
        "AVL BAL:",
        "ACCOUNT BALANCE IS",
        "BALANCE IS LOW",
        "LOW BALANCE",
        "REMAINING LIMIT",
        "OTP IS",
        "VERIFICATION CODE IS",
        "CODE TO LOGIN",
        "SECRET CODE",
        "DO NOT SHARE",
        "LOGIN ALERT",
        "DEVICE REGISTERED",
        "PASSWORD FORMAT",
        "KYC RECORD",
        "SERVICE REQUEST",
        "TELL US ABOUT",
        "FEEDBACK ON",
        "HOW DID YOU LIKE",
        "PACK IS EXPIRING",
        "VALID TILL",
        "PACK VALID TILL",
        "SMS COST:RS",
        "DATA IS CONSUMED",
        "STATEMENT GENERATED",
        "PIN FOR YOUR",
        "ACTIVATED ON",
        "LIMIT OPTIONS",
        "TDS BY EMPLOYER",
        "YOUR BAL IS",
        "ACCOUNT UPDATED",
        "LOGIN ATTEMPT",
        "PASSWORD RESET",
        "USERNAME FOR",
        "REQUEST FOR",
        "SUCCESSFULLY SET",
        "SIM BINDING CODE",
        "MBACTIVATE"
    )

    // Special case for Stage 1 OTP filter
    val otpPhrases = setOf(
        "OTP IS",
        "VERIFICATION CODE IS",
        "CODE TO LOGIN",
        "IS THE OTP FOR",
        "VERIFICATION CODE FOR",
        "DO NOT SHARE OTP",
        "VALID TILL",
        "NEVER SHARE YOUR OTP",
        "TRANSACTION PASSWORD IS",
        "MBACTIVATE",
        "SIM BINDING CODE"
    )

    val otpExcludePhrases = setOf(
        "WITHOUT OTP",
        "WITHOUT PIN",
        "NO OTP REQUIRED"
    )
    
    val strongInformationPhrases = setOf(
        "SUFFICIENT BALANCE",
        "CURRENT BALANCE IS",
        "AVAILABLE BALANCE",
        "AVL BAL:",
        "ACCOUNT BALANCE IS"
    )
}
