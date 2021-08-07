package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.ProductVariantRepository
import com.dukaankhata.server.dto.SaveProductVariant
import com.dukaankhata.server.dto.convertToString
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Product
import com.dukaankhata.server.entities.ProductVariant
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class ProductVariantProvider {

    @Autowired
    private lateinit var productVariantRepository: ProductVariantRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun saveProductVariant(product: Product, allProductVariants: List<SaveProductVariant>) : List<ProductVariant> {
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
                    newProductVariant.title = it.variantTitle ?: product.title
                    newProductVariant.mediaDetails = it.variantMediaDetails?.convertToString() ?: product.mediaDetails
                    newProductVariant.taxPerUnitInPaisa = it.variantTaxPerUnitInPaisa ?: product.taxPerUnitInPaisa
                    newProductVariant.originalPricePerUnitInPaisa = it.variantOriginalPricePerUnitInPaisa ?: product.originalPricePerUnitInPaisa
                    newProductVariant.sellingPricePerUnitInPaisa = it.variantSellingPricePerUnitInPaisa ?: product.sellingPricePerUnitInPaisa
                    newProductVariant.totalUnitInStock = it.variantTotalUnitInStock ?: product.totalUnitInStock
                    newProductVariant.colorInfo = it.variantColorInfo?.convertToString() ?: ""
                    newProductVariant.sizeInfo = it.variantSizeInfo?.convertToString() ?: ""
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
}
