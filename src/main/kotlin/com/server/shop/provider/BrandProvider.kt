package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.CommonProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils
import com.server.common.utils.DateUtils
import com.server.dk.dao.CompanyUsernameRepository
import com.server.dk.provider.EntityTrackingProvider
import com.server.dk.provider.ProductOrderProvider
import com.server.shop.dao.BrandRepository
import com.server.shop.dto.SaveBrandRequest
import com.server.shop.entities.Brand
import com.server.shop.entities.UserV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BrandProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var brandRepository: BrandRepository

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

    @Autowired
    private lateinit var companyV3Provider: CompanyV3Provider

    @Autowired
    private lateinit var commonProvider: CommonProvider

    fun getBrand(brandId: String): Brand? =
        try {
            brandRepository.findById(brandId).get()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Filed to get Brand for brandId: $brandId")
            null
        }

    fun saveBrand(request: SaveBrandRequest): Brand {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return saveBrand(userV3, request)
    }


    fun saveBrand(user: UserV3, request: SaveBrandRequest): Brand {
        val newBrand = Brand()
        val company = companyV3Provider.getCompany(request.companyId) ?: error("Company not found for id: ${request.companyId}")
        newBrand.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.BRD.name)
        newBrand.handle = request.handle?.let { it } ?: commonProvider.getUsernameFromName(request.marketingName, ReadableIdPrefix.BRD)
        newBrand.addedBy = user
        newBrand.company = company
        newBrand.logo = CommonUtils.convertToStringBlob(request.logo)
        newBrand.headerBanner = CommonUtils.convertToStringBlob(request.headerBanner)
        newBrand.legalName = request.legalName
        newBrand.marketingName = request.marketingName
        newBrand.dateOfEstablishment = DateUtils.parseEpochInSeconds(request.dateOfEstablishmentInSeconds)
        return brandRepository.save(newBrand)
    }
}
