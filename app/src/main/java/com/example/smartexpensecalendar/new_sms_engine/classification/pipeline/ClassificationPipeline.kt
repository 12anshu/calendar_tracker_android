package com.example.smartexpensecalendar.new_sms_engine.classification.pipeline

import com.example.smartexpensecalendar.new_sms_engine.classification.context.ClassificationContext
import com.example.smartexpensecalendar.new_sms_engine.classification.direction.DirectionClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.event.FinancialEventClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.messagetype.MessageTypeClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.mode.TransactionModeClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.models.ClassificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Executes the Classification phase.
 */
class ClassificationPipeline(

    private val directionClassifier: DirectionClassifier,
    private val messageTypeClassifier: MessageTypeClassifier,
    private val financialEventClassifier: FinancialEventClassifier,
    private val transactionModeClassifier: TransactionModeClassifier

) {

    fun execute(
        context: QualificationContext
    ): ClassificationContext {

        val direction =
            directionClassifier.classify(context)

        val messageType =
            messageTypeClassifier.classify(context)

        val event =
            financialEventClassifier.classify(context)

        val mode =
            transactionModeClassifier.classify(context)

        return ClassificationContext(
            qualification = context,
            classification = ClassificationResult(
                direction = direction,
                messageType = messageType,
                financialEvent = event,
                transactionMode = mode
            )
        )
    }
}