package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.ProductVariantRepository
import com.server.dk.dto.SaveProductVariantRequest
import com.server.dk.entities.*
import com.server.common.enums.ReadableIdPrefix
import com.server.dk.model.convertToString
import convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductVariantProvider {

    @Autowired
    private lateinit var productVariantRepository: ProductVariantRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    fun saveProductVariant(product: Product, allProductVariants: List<SaveProductVariantRequest>) : List<ProductVariant> {
        try {
            val productVariants = mutableListOf<ProductVariant>()
            // We also need to save the variants
            // If no variant is provided then save a default one with product details
            if (allProductVariants.isEmpty()) {
                val newProductVariant = ProductVariant()
                newProductVariant.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PVT.name)
                newProductVariant.addedBy = product.addedBy
                newProductVariant.company = product.company
                newProductVariant.title = product.title
                newProductVariant.product = product
                newProductVariant.mediaDetails = product.mediaDetails
                newProductVariant.taxPerUnitInPaisa = product.taxPerUnitInPaisa
                newProductVariant.originalPricePerUnitInPaisa = product.originalPricePerUnitInPaisa
                newProductVariant.sellingPricePerUnitInPaisa = product.sellingPricePerUnitInPaisa
                newProductVariant.totalUnitInStock = product.totalUnitInStock
                productVariants.add(productVariantRepository.save(newProductVariant))
            } else {
                allProductVariants.map {
                    val newProductVariant = ProductVariant()
                    newProductVariant.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PVT.name)
                    newProductVariant.addedBy = product.addedBy
                    newProductVariant.company = product.company
                    newProductVariant.product = product
                    newProductVariant.title = if (it.variantTitle != null && it.variantTitle.isNotEmpty()) it.variantTitle else product.title
                    newProductVariant.mediaDetails = it.variantMediaDetails?.convertToString() ?: product.mediaDetails
                    newProductVariant.taxPerUnitInPaisa = it.variantTaxPerUnitInPaisa ?: product.taxPerUnitInPaisa
                    newProductVariant.originalPricePerUnitInPaisa = it.variantOriginalPricePerUnitInPaisa ?: product.originalPricePerUnitInPaisa
                    newProductVariant.sellingPricePerUnitInPaisa = it.variantSellingPricePerUnitInPaisa ?: product.sellingPricePerUnitInPaisa
                    newProductVariant.totalUnitInStock = it.variantTotalUnitInStock ?: product.totalUnitInStock
                    newProductVariant.variantInfos = it.variantInfos.convertToString() ?: ""
                    productVariants.add(productVariantRepository.save(newProductVariant))
                }
            }
            return productVariants
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    fun getProductVariants(product: Product): List<ProductVariant> =
        try {
            productVariantRepository.findAllByProduct(product)
        } catch (e: Exception) {
            emptyList()
        }

    fun getProductVariant(productVariantId: String): ProductVariant? =
        try {
            productVariantRepository.findById(productVariantId).get()
        } catch (e: Exception) {
            null
        }

    fun getProductVariants(company: Company): List<ProductVariant> =
        try {
            productVariantRepository.findAllByCompany(company)
        } catch (e: Exception) {
            emptyList()
        }

    fun increaseClick(savedEntityTracking: EntityTracking) {
        val productVariant = savedEntityTracking.productVariant ?: return
        try {
            productVariant.totalClicksCount = (productVariant.totalClicksCount ?: 0) + 1
            productVariantRepository.save(productVariant)
            productProvider.increaseProductVariantClick(savedEntityTracking)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseView(savedEntityTracking: EntityTracking) {
        val productVariant = savedEntityTracking.productVariant ?: return
        try {
            productVariant.totalViewsCount = (productVariant.totalViewsCount ?: 0) + 1
            productVariantRepository.save(productVariant)
            productProvider.increaseProductVariantView(savedEntityTracking)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateOrderDetails(productOrder: ProductOrder) {
        val productVariants = productOrder.cartItems.mapNotNull { it.productVariant }
        try {
            val updatedProductVariants = productVariants.map {
                val prod = it
                prod.totalOrdersCount = (prod.totalOrdersCount ?: 0) + 1
                prod.totalOrderAmountInPaisa = (prod.totalOrderAmountInPaisa ?: 0) + productOrder.totalPricePayableInPaisa
                prod.totalUnitsOrdersCount = (prod.totalUnitsOrdersCount ?: 0) + productOrder.cartItems.sumBy { it.totalUnits.toInt() }
                prod
            }
            productVariantRepository.saveAll(updatedProductVariants)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
