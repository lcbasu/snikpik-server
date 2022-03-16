package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils.convertToStringBlob
import com.server.common.utils.DateUtils
import com.server.dk.dao.CompanyUsernameRepository
import com.server.dk.provider.EntityTrackingProvider
import com.server.dk.provider.ProductOrderProvider
import com.server.shop.dao.CompanyV3Repository
import com.server.shop.dto.SaveCompanyV3Request
import com.server.shop.entities.CompanyV3
import com.server.shop.entities.UserV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var companyV3Repository: CompanyV3Repository

    @Autowired
    private lateinit var companyUsernameRepository: CompanyUsernameRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var entityTrackingProvider: EntityTrackingProvider

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    fun getCompany(companyId: String): CompanyV3? =
        try {
            companyV3Repository.findById(companyId).get()
        } catch (e: Exception) {
            null
        }

    fun findByUser(user: UserV3): List<CompanyV3> {
        return companyV3Repository.findByAddedBy(user)
    }

    fun saveCompany(request: SaveCompanyV3Request): CompanyV3 {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return saveCompany(userV3, request)
    }

    fun saveCompany(userV3: UserV3, request: SaveCompanyV3Request): CompanyV3 {
        val newCompany = CompanyV3()
        newCompany.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.COM.name)
        newCompany.addedBy = userV3
        newCompany.logo = convertToStringBlob(request.logo)
        newCompany.headerBanner = convertToStringBlob(request.headerBanner)
        newCompany.legalName = request.legalName
        newCompany.marketingName = request.marketingName
        newCompany.dateOfEstablishment = DateUtils.parseEpochInSeconds(request.dateOfEstablishmentInSeconds)
        return companyV3Repository.save(newCompany)
    }
}
