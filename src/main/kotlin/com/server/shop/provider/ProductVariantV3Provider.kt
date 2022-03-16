package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils.convertToStringBlob
import com.server.shop.dao.ProductVariantV3Repository
import com.server.shop.dto.SaveProductV3Request
import com.server.shop.dto.SaveProductVariantV3Request
import com.server.shop.entities.ProductV3
import com.server.shop.entities.ProductVariantV3
import com.server.shop.entities.getAllProductCategories
import com.server.shop.enums.ProductVariantStatusV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductVariantV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productVariantV3Repository: ProductVariantV3Repository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var addressV3Provider: AddressV3Provider

    @Autowired
    private lateinit var productVariantCategoryV3Provider: ProductVariantCategoryV3Provider

    fun getProductVariants(product: ProductV3): List<ProductVariantV3> =
        try {
            productVariantV3Repository.findAllByProduct(product)
        } catch (e: Exception) {
            emptyList()
        }

    fun getProductVariant(productVariantId: String): ProductVariantV3? =
        try {
            productVariantV3Repository.findById(productVariantId).get()
        } catch (e: Exception) {
            null
        }


    fun saveProductVariants(savedProduct: ProductV3, request: SaveProductV3Request): List<ProductVariantV3> {
        return request.allProductVariants.map {
            saveProductVariant(savedProduct, it)
        }.filterNotNull()
    }

    fun saveProductVariant(savedProduct: ProductV3, request: SaveProductVariantV3Request): ProductVariantV3? {
        return try {
            val userV3 = savedProduct.addedBy ?: error("User not found for the product with id: ${savedProduct.addedBy?.id}")
            val addressV3 = request.locationId?.let { addressV3Provider.convertLocationToAddressV3(userV3, request.locationId) }

            val newProductVariant = ProductVariantV3()


            newProductVariant.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.PVT.name)

            newProductVariant.title = request.title
            newProductVariant.description = request.description ?: ""

            newProductVariant.mediaDetails = convertToStringBlob(request.mediaDetails)

            newProductVariant.variantInfos = convertToStringBlob(request.allVariantInfos)

            newProductVariant.properties = convertToStringBlob(request.allProductProperties)

            newProductVariant.specification = convertToStringBlob(request.specificationInfoList)

            newProductVariant.status = ProductVariantStatusV3.PENDING_APPROVAL
            newProductVariant.categories = savedProduct.categories

            newProductVariant.viewInRoomAllowed = request.viewInRoomAllowed

            newProductVariant.shippedFrom = addressV3
            newProductVariant.maxDeliveryDistanceInKm = request.maxDeliveryDistanceInKm
            newProductVariant.deliversOverIndia = request.deliversOverIndia

            newProductVariant.replacementAcceptable = request.replacementAcceptable
            newProductVariant.returnAcceptable = request.returnAcceptable

            newProductVariant.codAvailable = request.codAvailable

            newProductVariant.mrpPerUnitInPaisa = (request.mrpInRupees * 100).toLong()
            newProductVariant.sellingPricePerUnitInPaisa = (request.sellingPriceInRupees * 100).toLong()

            newProductVariant.minOrderUnitCount = request.minOrderUnitCount
            newProductVariant.maxOrderPerUser = request.maxOrderPerUser

            // Difference between totalUnitInStock and totalSoldUnits gives the total available units
            newProductVariant.totalUnitInStock = request.totalUnitInStock
            newProductVariant.totalSoldUnits = 0
            newProductVariant.totalSoldAmountInPaisa = 0
            newProductVariant.totalOrdersCount = 0

            newProductVariant.product = savedProduct
            newProductVariant.addedBy = userV3

            val savedProductVariant = productVariantV3Repository.save(newProductVariant)

            productVariantCategoryV3Provider.saveProductCategories(savedProductVariant, savedProduct.getAllProductCategories())

            savedProductVariant
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveProductVariant error", e)
            null
        }
    }

}
