package com.example.smartexpensecalendar.new_sms_engine.entity.framework.model

import com.example.smartexpensecalendar.new_sms_engine.common.enums.DiscoveryMethod

/**
 * Represents a text fragment discovered during the Discovery stage.
 */
data class EntityWindow(

    /**
     * Original text discovered from the normalized message.
     */
    val text: String,

    /**
     * Start index within the normalized message.
     */
    val startIndex: Int,

    /**
     * End index within the normalized message.
     */
    val endIndex: Int,

    /**
     * Method used to discover this window.
     */
    val discoveryMethod: DiscoveryMethod,

    /**
     * Additional discovery metadata.
     */
    val metadata: Map<String, String> = emptyMap()
)