package com.example.smartexpensecalendar.new_sms_engine.classification.builders

import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object SemanticEvidenceBuilder {

    private val COMPLETED_ACTIONS = setOf(
        TokenCategory.DEBIT_ACTION,
        TokenCategory.CREDIT_ACTION,
        TokenCategory.TRANSACTION_ACTION,
        TokenCategory.REFUND_ACTION
    )

    fun build(tokens: List<Token>): List<Evidence> {

        val evidence = mutableListOf<Evidence>()

        val categories = tokens
            .flatMap { it.categories }
            .toSet()

        val hasLiabilityObject =
            TokenCategory.LIABILITY_OBJECT in categories

        val hasLiabilityState =
            TokenCategory.LIABILITY_STATE in categories

        val hasRequestAction =
            TokenCategory.REQUEST_ACTION in categories

        val hasScheduling =
            TokenCategory.TEMPORAL_SCHEDULING in categories

        val hasFailure =
            TokenCategory.FAILURE in categories

        val hasAuthentication =
            TokenCategory.AUTHENTICATION in categories

        val hasAuthorization =
            TokenCategory.AUTHORIZATION in categories

        val hasStatement =
            TokenCategory.STATEMENT in categories

        val hasAccountUpdate =
            TokenCategory.ACCOUNT_UPDATE in categories

        val hasSecurity =
            TokenCategory.SECURITY in categories

        val hasReward =
            TokenCategory.REWARD in categories

        if (hasLiabilityObject && hasLiabilityState) {
            evidence += Evidence(
                type = EvidenceType.OBLIGATION_CONTEXT,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SEMANTIC_LIABILITY",
                matchedText = ""
            )
        }

        if (hasRequestAction && hasLiabilityState) {
            evidence += Evidence(
                type = EvidenceType.OBLIGATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SEMANTIC_REQUEST",
                matchedText = ""
            )
        }

        if (hasLiabilityObject && hasScheduling) {
            evidence += Evidence(
                type = EvidenceType.OBLIGATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SEMANTIC_SCHEDULING",
                matchedText = ""
            )
        }

        if (hasFailure) {

            evidence += Evidence(
                type = EvidenceType.FAILURE_STATUS,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasAuthentication) {

            evidence += Evidence(
                type = EvidenceType.AUTHENTICATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasAuthorization) {

            evidence += Evidence(
                type = EvidenceType.AUTHORIZATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasStatement) {

            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasAccountUpdate) {

            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasSecurity) {

            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasReward) {

            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.MEDIUM,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        return evidence
    }
}