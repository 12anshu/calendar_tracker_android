package com.example.smartexpensecalendar.new_sms_engine.classification

import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageTypeResult

/**
 * Converts evidence into a financial message type.
 *
 * This class contains ONLY business classification rules.
 */
object MessageTypeClassifier {

    fun classify(
        evidence: List<Evidence>
    ): MessageTypeResult {

        val evidenceTypes = evidence
            .map(Evidence::type)
            .toSet()

        val messageType = classifyInternal(evidenceTypes)

        return MessageTypeResult(
            messageType = messageType,
            evidence = evidence
        )
    }

    /**
     * Business classification rules.
     */
    private fun classifyInternal(
        evidence: Set<EvidenceType>
    ): MessageType {

        val hasAuthenticationContext =
            EvidenceType.AUTHENTICATION_CONTEXT in evidence

        val hasNegatedAuthenticationContext =
            EvidenceType.NEGATED_AUTHENTICATION_CONTEXT in evidence

        return when {

            // Explicit future action
            EvidenceType.FUTURE_ACTION in evidence ->

                MessageType.OBLIGATION

            // Scheduling / payment reminder
            EvidenceType.OBLIGATION_CONTEXT in evidence ->

                MessageType.OBLIGATION

            // MAB advisory is informational unless the message
            // also contains an independently completed financial movement.
            EvidenceType.MAB_ADVISORY_CONTEXT in evidence &&
                    EvidenceType.COMPLETED_ACTION !in evidence ->

                MessageType.INFORMATION

            // Informational financial message
            EvidenceType.INFORMATION_CONTEXT in evidence ->

                MessageType.INFORMATION

            // Completed financial activity
            (EvidenceType.ACCOUNT_MOVEMENT in evidence)
                    &&
            EvidenceType.FAILURE_STATUS !in evidence &&
                    (
                            !hasAuthenticationContext || hasNegatedAuthenticationContext
                    ) &&
                    EvidenceType.PENDING_STATUS !in evidence &&
            EvidenceType.AUTHORIZATION_CONTEXT !in evidence ->

                MessageType.TRANSACTION

            else ->

                MessageType.UNKNOWN
        }
    }
}