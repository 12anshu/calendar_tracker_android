package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.discovery

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.common.enums.DiscoveryMethod
import com.example.smartexpensecalendar.new_sms_engine.common.utils.MatchUtils
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow
import com.example.smartexpensecalendar.new_sms_engine.entity.merchant.registry.MerchantRegistry

/**
 * Discovers merchants by matching against the Merchant Registry.
 */
class KnownMerchantDiscoveryStrategy : MerchantDiscoveryStrategy {

    override fun discover(
        context: ExtractionContext
    ): List<EntityWindow> {

        val message = MatchUtils.normalize(
            context.qualificationContext.input.message
        )
        val windows = mutableListOf<EntityWindow>()

        MerchantRegistry.getAll().forEach { merchant ->

            var searchIndex = 0

            while (true) {

                val index = message.indexOf(
                    merchant,
                    searchIndex,
                    ignoreCase = true
                )

                if (index == -1) break

                windows.add(
                    EntityWindow(
                        text = merchant,
                        startIndex = index,
                        endIndex = index + merchant.length,
                        discoveryMethod = DiscoveryMethod.REGISTRY_MATCH,
                        metadata = mapOf(
                            "registry" to "merchant"
                        )
                    )
                )

                searchIndex = index + merchant.length
            }
        }

        return windows
    }
}