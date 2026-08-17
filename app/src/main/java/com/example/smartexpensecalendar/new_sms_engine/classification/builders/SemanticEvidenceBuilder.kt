package com.example.smartexpensecalendar.new_sms_engine.classification.builders

import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object SemanticEvidenceBuilder {

    private fun Set<TokenCategory>.containsAny(categories: Set<TokenCategory>): Boolean =
        any { it in categories }


    private val EXPLICIT_FINANCIAL_ACTIONS = setOf(
        TokenCategory.DEBIT_ACTION,
        TokenCategory.CREDIT_ACTION,
        TokenCategory.TRANSACTION_ACTION,
        TokenCategory.REFUND_ACTION
    )

    private val FINANCIAL_INSTRUMENTS = setOf(
        TokenCategory.PAYMENT_MODE
    )

    private val TRANSFER_RELATIONS = setOf(
        TokenCategory.PREPOSITION_SOURCE,
        TokenCategory.PREPOSITION_DESTINATION
    )

    private val COMPLETION_CONTEXT = setOf(
        TokenCategory.SUCCESS_STATUS
    )

    private fun Evidence.overlaps(other: Evidence): Boolean {

        if (startIndex < 0 || endIndex < 0) return false
        if (other.startIndex < 0 || other.endIndex < 0) return false

        return startIndex <= other.endIndex &&
                endIndex >= other.startIndex
    }

    private fun Evidence.isActionStateEvidence(): Boolean =
        type == EvidenceType.NEGATED_ACTION ||
                type == EvidenceType.CONDITIONAL_ACTION


    private fun List<Evidence>.isValidCompletedAction(
        completed: Evidence
    ): Boolean {

        return none { state ->
            state.isActionStateEvidence() &&
                    state.overlaps(completed)
        }
    }

    private fun hasBlockedFinancialAction(
        tokens: List<Token>,
        patternEvidence: List<Evidence>
    ): Boolean {

        val stateEvidence = patternEvidence.filter {
            it.isActionStateEvidence()
        }

        if (stateEvidence.isEmpty()) return false

        return tokens.indices.any { index ->

            tokens[index].hasAny(EXPLICIT_FINANCIAL_ACTIONS) &&
                    stateEvidence.any { state ->
                        state.startIndex <= index &&
                                index <= state.endIndex
                    }
        }
    }

    fun build(tokens: List<Token>, patternEvidence: List<Evidence>): List<Evidence> {

        val evidence = mutableListOf<Evidence>()

        val categories = tokens
            .flatMap { it.categories }
            .toSet()

        val hasAccountBalance =
            TokenCategory.ACCOUNT_BALANCE in categories

        val hasExplicitFinancialAction =
            categories.containsAny(EXPLICIT_FINANCIAL_ACTIONS)

        val completedActionEvidence =
            patternEvidence.filter {
                it.type == EvidenceType.COMPLETED_ACTION
            }

        val hasValidCompletedPattern =
            completedActionEvidence.any {
                patternEvidence.isValidCompletedAction(it)
            }

        val hasBlockedFinancialAction =
            hasBlockedFinancialAction(
                tokens,
                patternEvidence
            )

        val hasCurrency =
            TokenCategory.CURRENCY in categories

        val hasFinancialInstrument =
            categories.containsAny(FINANCIAL_INSTRUMENTS)

        val hasTransferRelation =
            categories.containsAny(TRANSFER_RELATIONS)

        val hasSuccess =
            categories.containsAny(COMPLETION_CONTEXT)

        val hasStructuredFinancialMovement =
            hasCurrency &&
                    hasFinancialInstrument &&
                    hasTransferRelation

        val hasLiabilityObject =
            TokenCategory.LIABILITY_OBJECT in categories

        val hasLiabilityState =
            TokenCategory.LIABILITY_STATE in categories

        val hasRequestAction =
            TokenCategory.REQUEST_ACTION in categories

        val hasScheduling =
            TokenCategory.TEMPORAL_SCHEDULING in categories

        val hasFailureStatus =
            TokenCategory.FAILURE_STATUS in categories

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

        val hasRetirement =
            TokenCategory.RETIREMENT in categories

        val hasInvestment =
            TokenCategory.INVESTMENT in categories

        val hasTax =
            TokenCategory.TAX in categories

        val hasDocument =
            TokenCategory.DOCUMENT in categories

        val hasLoanLifecycle =
            TokenCategory.LOAN_LIFECYCLE in categories

        val hasProcessState =
            TokenCategory.PROCESS_STATE in categories


        val hasPreDisbursalWorkflow =
            patternEvidence.any {
                it.type == EvidenceType.PRE_DISBURSAL_WORKFLOW
            }

        val hasPendingStatus =
            TokenCategory.PENDING_STATUS in categories

        val hasPendingRefund =
            TokenCategory.REFUND_ACTION in categories &&
                    hasPendingStatus

        val hasFutureAuxiliary =
            TokenCategory.AUXILIARY_FUTURE in categories

        val hasExplicitMovementAction =
            categories.containsAny(
                setOf(
                    TokenCategory.DEBIT_ACTION,
                    TokenCategory.CREDIT_ACTION,
                    TokenCategory.REFUND_ACTION,
//                    TokenCategory.TRANSACTION_ACTION
                )
            )

        val hasStateContradictingFallback =
            hasFailureStatus || hasPendingRefund ||
                    (
                            hasPendingStatus &&
                                    !hasExplicitMovementAction
                            ) ||
                    (
                            hasFutureAuxiliary &&
                                    !hasExplicitMovementAction
                            )

        val hasChargeRevision =
            patternEvidence.any {
                it.type == EvidenceType.INFORMATION_CONTEXT &&
                        (
                                it.source == "CHARGE_REVISION" ||
                                        it.source == "FUTURE_REVISION" ||
                                        it.source == "GOING_TO_REVISION"
                                )
            }


        val hasNonMonetaryReward =
            hasReward && !hasCurrency

        val hasStrongCompletedFinancialMovement =
            hasValidCompletedPattern &&
                    hasExplicitFinancialAction &&
                    hasFinancialInstrument

        val hasStatementDelivery =
            patternEvidence.any {
                it.type == EvidenceType.INFORMATION_CONTEXT &&
                        it.source == "STATEMENT_DELIVERY"
            }

        if (hasPendingStatus) {
            evidence += Evidence(
                type = EvidenceType.PENDING_STATUS,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (
            hasValidCompletedPattern &&
            hasExplicitFinancialAction &&
            hasFinancialInstrument &&
            !hasPendingRefund &&
            !hasNonMonetaryReward &&
            !hasStatementDelivery
        ) {
            evidence += Evidence(
                type = EvidenceType.ACCOUNT_MOVEMENT,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SEMANTIC_ACCOUNT_MOVEMENT",
                matchedText = ""
            )
        }

        if (
            hasStructuredFinancialMovement &&
            !hasBlockedFinancialAction &&
            !hasPreDisbursalWorkflow &&
            !hasStateContradictingFallback &&
            !hasChargeRevision &&
            !hasNonMonetaryReward &&
            !hasStatementDelivery
        ) {
            evidence += Evidence(
                type = EvidenceType.ACCOUNT_MOVEMENT,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SEMANTIC_ACCOUNT_MOVEMENT",
                matchedText = ""
            )
        }

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

        if (hasFailureStatus) {
            evidence += Evidence(
                type = EvidenceType.FAILURE_STATUS,
                strength = EvidenceStrength.VERY_HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasPendingStatus) {
            evidence += Evidence(
                type = EvidenceType.PENDING_STATUS,
                strength = EvidenceStrength.HIGH,
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

        if (
            hasStatement &&
            (
                    !hasStrongCompletedFinancialMovement ||
                            hasStatementDelivery
                    )
        ) {
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

        if (hasLoanLifecycle && hasProcessState) {
            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasLoanLifecycle && hasAuthorization) {
            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasRetirement && hasValidCompletedPattern) {
            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (
            hasInvestment &&
            (
                    hasProcessState ||
                            (
                                    hasValidCompletedPattern &&
                                            !hasStrongCompletedFinancialMovement
                                    )
                    )
        ) {
            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }

        if (hasTax && hasValidCompletedPattern) {
            evidence += Evidence(
                type = EvidenceType.INFORMATION_CONTEXT,
                strength = EvidenceStrength.HIGH,
                source = "SemanticEvidenceBuilder",
                matchedText = ""
            )
        }
        return evidence
    }
}