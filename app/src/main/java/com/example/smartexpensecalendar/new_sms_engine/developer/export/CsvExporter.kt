package com.example.smartexpensecalendar.new_sms_engine.developer.export

import java.lang.StringBuilder
import kotlin.String

object CsvExporter {

    fun export(
        rows: List<AnalyzerExportRow>
    ): String {

        val builder = StringBuilder()

        val headers = listOf(
            "Date", "Sender", "Message", 
            "Qualified",
//            "QualificationReason",
//            "Tokens", "TokenCategories",
//            "MatchedPatterns", "GeneratedEvidence",
            "MessageType",
            "Amount",
//            "AmountConfidence",
            "Direction",
//            "DirectionConfidence",
            "PaymentMode",
//            "PaymentModeConfidence",
            "Merchant",
            "MerchantSegment",
            "MerchantConfidence",
            "MerchantAnchorUsed",
            "Category",
            "CategoryConfidence",
            "CategoryEvidence",
//            "Account", "Reference",
            "MessageSegment"
        )

        builder.appendLine(headers.joinToString(","))

        rows.forEach { row ->
            builder.appendLine(
                listOf(
                    escape(row.date),
                    escape(row.sender),
                    escape(row.message),
                    row.qualified,
//                    escape(row.qualificationReason),
//                    escape(row.tokens),
//                    escape(row.tokenCategories),
//                    escape(row.matchedPatterns),
//                    escape(row.evidence),
                    escape(row.messageType),
                    escape(row.amount),
//                    row.amountConfidence,
                    row.direction,
//                    row.directionConfidence,
                    row.paymentMode,
//                    row.paymentModeConfidence,
                    escape(row.merchant),
                    escape(row.merchantSourceSegment),
                    row.merchantConfidence,
                    escape(row.merchantAnchorUsed),
                    escape(row.category),
                    escape(row.categoryConfidence),
                    escape(row.categoryEvidence),
//                    escape(row.account),
//                    escape(row.reference),
                    escape(row.messageSegments)
                ).joinToString(",")
            )
        }

        return builder.toString()
    }

    private fun escape(value: String?): String {

        return "\"" +
                value.orEmpty()
                    .replace("\"", "\"\"")
                    .replace("\r\n", "\n")
                    .replace("\r", "\n") +
                "\""
    }
}
