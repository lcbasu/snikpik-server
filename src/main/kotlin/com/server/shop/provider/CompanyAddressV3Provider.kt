package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.CompanyAddressV3Repository
import com.server.shop.entities.CompanyAddressKeyV3
import com.server.shop.entities.CompanyAddressV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyAddressV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var companyAddressV3Repository: CompanyAddressV3Repository


    fun getBrandPolicyKey(companyId: String, addressId: String): CompanyAddressKeyV3 {
        val key = CompanyAddressKeyV3()
        key.companyId = companyId
        key.addressId = addressId
        return key
    }

    fun getCompanyAddressV3(companyId: String, addressId: String): CompanyAddressV3? =
        try {
            val key = getBrandPolicyKey(companyId = companyId, addressId = addressId)
            companyAddressV3Repository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getCompanyAddressV3List(companyId: String): List<CompanyAddressV3> =
        try {
            companyAddressV3Repository.findAllByCompanyId(companyId)
        } catch (e: Exception) {
            emptyList()
        }
}
