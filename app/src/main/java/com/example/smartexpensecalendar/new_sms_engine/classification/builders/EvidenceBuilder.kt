package com.example.smartexpensecalendar.new_sms_engine.classification.builders

import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

/**
 * Produces semantic evidence from tokenized SMS.
 */
interface EvidenceBuilder {

    fun build(
        tokens: List<Token>
    ): List<Evidence>

}