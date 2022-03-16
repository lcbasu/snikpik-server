package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.BrandDiscountRepository
import com.server.shop.entities.BrandDiscount
import com.server.shop.entities.BrandDiscountKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BrandDiscountProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var brandDiscountRepository: BrandDiscountRepository


    fun getBrandDiscountKey(brandId: String, discountId: String): BrandDiscountKey {
        val key = BrandDiscountKey()
        key.brandId = brandId
        key.discountId = discountId
        return key
    }

    fun getBrandDiscount(brandId: String, discountId: String): BrandDiscount? =
        try {
            val key = getBrandDiscountKey(brandId = brandId, discountId = discountId)
            brandDiscountRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getBrandDiscounts(brandId: String): List<BrandDiscount> =
        try {
            brandDiscountRepository.findAllByBrandId(brandId)
        } catch (e: Exception) {
            emptyList()
        }
}
