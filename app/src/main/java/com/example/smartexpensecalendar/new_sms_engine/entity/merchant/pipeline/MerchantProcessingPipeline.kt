package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.pipeline

import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityAssessor
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityDiscovery
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityNormalizer
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityResolver
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.pipeline.EntityProcessingPipeline

/**
 * Merchant implementation of the Entity Processing Pipeline.
 */
class MerchantProcessingPipeline(

    discovery: EntityDiscovery,
    assessor: EntityAssessor,
    resolver: EntityResolver,
    normalizer: EntityNormalizer

) : EntityProcessingPipeline(
    discovery,
    assessor,
    resolver,
    normalizer
)