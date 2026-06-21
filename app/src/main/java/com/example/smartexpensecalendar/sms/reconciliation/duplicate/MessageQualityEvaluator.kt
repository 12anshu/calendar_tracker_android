package com.example.smartexpensecalendar.sms.reconciliation.duplicate

object MessageQualityEvaluator {

    fun evaluate(body: String): MessageQuality {
        val upper = body.uppercase()
        
        return when {
            // AUTHORITATIVE: high-certainty confirmation verbs
            upper.contains("SUCCESSFUL") || 
            upper.contains("COMPLETED") || 
            upper.contains("TRANSACTION SUCCESSFUL") ||
            upper.contains("SUCCESSFULLY") -> MessageQuality.AUTHORITATIVE
            
            // HIGH: direct debited/credited verbs
            upper.contains("DEBITED") || 
            upper.contains("CREDITED") -> MessageQuality.HIGH
            
            // NORMAL: technical processing verbs
            upper.contains("PROCESSED") || 
            upper.contains("SUBMITTED") -> MessageQuality.NORMAL
            
            // LOW: initiation or pending state verbs
            upper.contains("INITIATED") || 
            upper.contains("PENDING") -> MessageQuality.LOW
            
            else -> MessageQuality.NORMAL
        }
    }
}
