package com.example.smartexpensecalendar.new_sms_engine.common.utils


fun Enum<*>.displayName(): String {
    return name.replace('_', ' ')
}