package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.CompanyPolicyRepository
import com.server.shop.entities.CompanyPolicy
import com.server.shop.entities.CompanyPolicyKey
import com.server.shop.enums.PolicyType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyPolicyProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var companyPolicyRepository: CompanyPolicyRepository

    fun getBrandPolicyKey(companyId: String, policyType: PolicyType): CompanyPolicyKey {
        val key = CompanyPolicyKey()
        key.companyId = companyId
        key.policyType = policyType
        return key
    }

    fun getCompanyPolicy(companyId: String, policyType: PolicyType): CompanyPolicy? =
        try {
            val key = getBrandPolicyKey(companyId = companyId, policyType = policyType)
            companyPolicyRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getCompanyPolicyList(companyId: String): List<CompanyPolicy> =
        try {
            companyPolicyRepository.findAllByCompanyId(companyId)
        } catch (e: Exception) {
            emptyList()
        }
}
