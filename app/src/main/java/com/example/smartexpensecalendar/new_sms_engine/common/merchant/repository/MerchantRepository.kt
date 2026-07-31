package com.example.smartexpensecalendar.new_sms_engine.common.merchant.repository

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantDefinition

interface MerchantRepository {

    fun findMerchant(merchant: String?): MerchantDefinition?
}