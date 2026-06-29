package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.discovery

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

/**
 * Contract for merchant discovery strategies.
 */
fun interface MerchantDiscoveryStrategy {

    /**
     * Discovers merchant windows using a specific strategy.
     */
    fun discover(
        context: ExtractionContext
    ): List<EntityWindow>
}