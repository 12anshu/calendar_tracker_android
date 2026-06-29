package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.discovery

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityDiscovery
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

/**
 * Discovers potential merchant windows from a transaction message.
 */
class MerchantDiscovery(

    private val strategies: List<MerchantDiscoveryStrategy>

) : EntityDiscovery {

    override fun discover(
        context: ExtractionContext
    ): List<EntityWindow> {

        return strategies
            .flatMap { it.discover(context) }
            .distinctBy { Triple(it.startIndex, it.endIndex, it.text.lowercase()) }
            .sortedBy { it.startIndex }
    }
}