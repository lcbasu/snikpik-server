package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.CompanyDiscountV3Repository
import com.server.shop.entities.CompanyDiscountKeyV3
import com.server.shop.entities.CompanyDiscountV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyDiscountV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var companyDiscountV3Repository: CompanyDiscountV3Repository


    fun getBrandPolicyKey(companyId: String, discountId: String): CompanyDiscountKeyV3 {
        val key = CompanyDiscountKeyV3()
        key.companyId = companyId
        key.discountId = discountId
        return key
    }

    fun getCompanyDiscountV3(companyId: String, discountId: String): CompanyDiscountV3? =
        try {
            val key = getBrandPolicyKey(companyId = companyId, discountId = discountId)
            companyDiscountV3Repository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getCompanyDiscountV3List(companyId: String): List<CompanyDiscountV3> =
        try {
            companyDiscountV3Repository.findAllByCompanyId(companyId)
        } catch (e: Exception) {
            emptyList()
        }
}
