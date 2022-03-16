package com.server.shop.provider

import com.server.shop.dao.ProductCategoryV3Repository
import com.server.shop.entities.AllProductCategories
import com.server.shop.entities.ProductCategoryV3Entity
import com.server.shop.entities.ProductCategoryV3Key
import com.server.shop.entities.ProductV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductCategoryV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productCategoryV3Repository: ProductCategoryV3Repository

    fun saveProductCategories(savedProduct: ProductV3, allProductCategories: AllProductCategories): List<ProductCategoryV3Entity> {
        return allProductCategories.categories.map {
            val key = ProductCategoryV3Key()
            key.category = it
            key.productId = savedProduct.id
            val productCategoryV3Entity = ProductCategoryV3Entity()
            productCategoryV3Entity.id = key
            productCategoryV3Entity.product = savedProduct
            productCategoryV3Repository.save(productCategoryV3Entity)
        }
    }
}
