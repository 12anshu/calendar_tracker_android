package com.example.smartexpensecalendar.sms_engine.model


interface Extractor<T> {

    fun extract(
        smsText: String
    ): ExtractionResult<T>
}