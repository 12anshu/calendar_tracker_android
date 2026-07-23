package com.example.smartexpensecalendar.new_sms_engine.classification.model

data class ClassificationEvidence(

    val liabilityObject: Boolean = false,

    val liabilityState: Boolean = false,

    val requestAction: Boolean = false,

    val scheduling: Boolean = false,

    val completedAction: Boolean = false
)