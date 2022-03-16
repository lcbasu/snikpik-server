package com.server.shop.provider

import com.server.shop.dao.ProductVariantCategoryV3Repository
import com.server.shop.entities.AllProductCategories
import com.server.shop.entities.ProductVariantCategoryV3
import com.server.shop.entities.ProductVariantCategoryV3Key
import com.server.shop.entities.ProductVariantV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductVariantCategoryV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productVariantCategoryV3Repository: ProductVariantCategoryV3Repository

    fun saveProductCategories(savedProductVariant: ProductVariantV3, allProductCategories: AllProductCategories): List<ProductVariantCategoryV3> {
        return allProductCategories.categories.map {
            val key = ProductVariantCategoryV3Key()
            key.category = it
            key.productVariantId = savedProductVariant.id
            val productVariantCategoryV3 = ProductVariantCategoryV3()
            productVariantCategoryV3.id = key
            productVariantCategoryV3.productVariant = savedProductVariant
            productVariantCategoryV3Repository.save(productVariantCategoryV3)
        }
    }
}
