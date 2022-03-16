package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.BrandPolicyRepository
import com.server.shop.entities.BrandPolicy
import com.server.shop.entities.BrandPolicyKey
import com.server.shop.enums.PolicyType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BrandPolicyProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var brandPolicyRepository: BrandPolicyRepository


    @Autowired
    private lateinit var brandProvider: BrandProvider


    fun getBrandPolicyKey(brandId: String, policyType: PolicyType): BrandPolicyKey {
        val key = BrandPolicyKey()
        key.brandId = brandId
        key.policyType = policyType
        return key
    }

    fun getBrandPolicy(brandId: String, policyType: PolicyType): BrandPolicy? =
        try {
            val key = getBrandPolicyKey(brandId = brandId, policyType = policyType)
            brandPolicyRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getBrandPolicies(brandId: String): List<BrandPolicy> =
        try {
            brandPolicyRepository.findAllByBrandId(brandId)
        } catch (e: Exception) {
            emptyList()
        }
}
