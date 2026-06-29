package com.example.smartexpensecalendar.new_sms_engine.common.enums

/**
 * Indicates how an entity window was discovered.
 */
enum class DiscoveryMethod {

    AFTER_PREPOSITION,

    BEFORE_PREPOSITION,

    LINE_PATTERN,

    STRUCTURAL,

    REGISTRY_MATCH,

    REGEX,

    CONTEXTUAL,

    CUSTOM
}