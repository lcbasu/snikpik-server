package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils.convertToStringBlob
import com.server.shop.dao.ProductV3Repository
import com.server.shop.dto.*
import com.server.shop.entities.PostTaggedProduct
import com.server.shop.entities.ProductV3
import com.server.shop.entities.ProductVariantV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.ProductStatusV3
import com.server.shop.pagination.SQLSlice
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ProductV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productV3Repository: ProductV3Repository

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productVariantV3Provider: ProductVariantV3Provider

    @Autowired
    private lateinit var productCategoryV3Provider: ProductCategoryV3Provider

    @Autowired
    private lateinit var brandProvider: BrandProvider

    @Autowired
    private lateinit var bookmarkedProductsV3Provider: BookmarkedProductsV3Provider

    @Autowired
    private lateinit var postTaggedProductsProvider: PostTaggedProductsProvider

    fun getProduct(productId: String): ProductV3? =
        try {
            productV3Repository.findById(productId).get()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Filed to get Product for Id: $productId")
            null
        }

    fun saveProduct(request: SaveProductV3Request): ProductV3? {
        return try {
            val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
            saveProduct(userV3, request)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveProduct error", e)
            null
        }
    }

    fun saveProduct(user: UserV3, request: SaveProductV3Request): ProductV3? {
        return try {
            val brand = request.brandId?.let { brandProvider.getBrand(request.brandId) }
            val newProduct = ProductV3()
            newProduct.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.PRD.name)
            newProduct.status = ProductStatusV3.PENDING_APPROVAL
            newProduct.categories = convertToStringBlob(request.allProductCategories)
            newProduct.productUnit = request.productUnit
            newProduct.addedBy = user
            newProduct.brand = brand
            newProduct.company = brand?.company
            val product = productV3Repository.save(newProduct)
            productCategoryV3Provider.saveProductCategories(product, request.allProductCategories)
            val variants = productVariantV3Provider.saveProductVariants(product, request)
            val firstVariant = variants.firstOrNull()
            product.defaultVariant = firstVariant
            // Save the default variant
            productV3Repository.save(product)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveProduct error", e)
            null
        }
    }

    fun updateBookmarkProductVariant(request: BookmarkProductVariantV3Request): List<ProductVariantV3> {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        val variant = productVariantV3Provider.getProductVariant(request.productVariantId) ?: error("Product variant not found for id: ${request.productVariantId}")
        bookmarkedProductsV3Provider.updateBookmark(userV3, variant)
        return bookmarkedProductsV3Provider.getValidBookmarks(userV3).mapNotNull { it.productVariant }
    }

    fun getBookmarkedProductVariants(): List<ProductVariantV3> {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return bookmarkedProductsV3Provider.getValidBookmarks(userV3).mapNotNull { it.productVariant }
    }

    fun getCreatorsInFocus(): List<UserV3> {
        val usersWithAnyProduct = productV3Repository.findAll().mapNotNull { it?.addedBy }
        val takenIds = mutableSetOf<String>()
        val takenUsers = mutableListOf<UserV3>()
        usersWithAnyProduct.forEach {
            if (!takenIds.contains(it.id)) {
                takenIds.add(it.id)
                takenUsers.add(it)
            }
        }
        return takenUsers
    }

    fun getFeaturedProducts(request: FeaturedProductsRequest): SQLSlice<ProductV3> {
        // TODO: Add logic to get relevant Featured products
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getSimilarProducts(request: SimilarProductsRequest): SQLSlice<ProductV3> {
        // TODO: Add logic to get relevant products
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getDecorProducts(request: DecorProductsRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getUnboxManagedProducts(request: UnboxManagedProductsRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getFeaturedProductsForCategory(request: FeaturedProductsForCategoryRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getRecentSearchedProductsForCategory(request: RecentSearchedProductsForCategoryRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getTrendyOfferProductsForCategory(request: TrendyOfferProductsForCategoryRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getAllProductsForCategory(request: AllProductsForCategoryRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getAllProductsForSubCategory(request: AllProductsForSubCategoryRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getAllProductsForVertical(request: AllProductsForVerticalRequest): SQLSlice<ProductV3> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllBy(pageable))
    }

    fun getProductsByCreator(request: ProductsByCreatorRequest): SQLSlice<ProductV3> {
        val user = userV3Provider.getUserV3(request.creatorUserId) ?: error("User not found for id: ${request.creatorUserId}")
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(productV3Repository.findAllByAddedBy(user, pageable))
    }

    fun getPostTaggedProducts(request: PostTaggedProductsRequest): SQLSlice<PostTaggedProduct> {
        return postTaggedProductsProvider.getPostTaggedProducts(request)
    }

    fun getCommissionDetailsForTaggedProducts(request: CommissionDetailsForTaggedProductsRequest): CommissionDetailsForTaggedProductsResponse {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        val products = request.taggedProductsIds.mapNotNull { getProduct(it) }

        // Own Inventory
        val ownProducts = products.filter { it.addedBy?.id == userV3.id }
        val ownProductsIds = ownProducts.map { it.id }
        val ownProductsResponse = mutableListOf<OwnSingleProductCommissionResponse>()
        val ownTotalAmountOfProductsInPaisa = ownProducts.sumOf { it.defaultVariant?.sellingPricePerUnitInPaisa ?: 0 }

        var maxCutUnboxTakesInPercentage: Double = 0.0
        var minCutUnboxTakesInPercentage: Double = 0.0
        var totalCutUnboxTakesInPaisa: Long = 0

        ownProducts.map {
            val unboxTakesCommissionPercentage = it.defaultVariant?.unboxTakesCommissionPercentage ?: 0.0
            if (unboxTakesCommissionPercentage > maxCutUnboxTakesInPercentage) {
                maxCutUnboxTakesInPercentage = unboxTakesCommissionPercentage
            }
            if (unboxTakesCommissionPercentage < minCutUnboxTakesInPercentage) {
                minCutUnboxTakesInPercentage = unboxTakesCommissionPercentage
            }
            val commissionPaisaFromPercentage = it.defaultVariant?.sellingPricePerUnitInPaisa?.times(unboxTakesCommissionPercentage)?.div(100)?.times(100)?.toLong() ?: 0
            val cutUnboxTakesInPaisa = if (commissionPaisaFromPercentage > (it.defaultVariant?.unboxTakesMaxCommissionInPaisa ?: 0)) {
                it.defaultVariant?.unboxTakesMaxCommissionInPaisa ?: 0
            } else {
                commissionPaisaFromPercentage
            }
            totalCutUnboxTakesInPaisa += cutUnboxTakesInPaisa
            ownProductsResponse.add(OwnSingleProductCommissionResponse(
                product = it.toSaveProductV3Response(),
                cutUnboxTakesInPaisa = cutUnboxTakesInPaisa,
            ))
        }

        // Unbox Inventory
        val unboxProducts = products.filterNot { ownProductsIds.contains(it.id) }

        val unboxProductsResponse = mutableListOf<UnboxSingleProductCommissionResponse>()
        val unboxTotalAmountOfProductsInPaisa = unboxProducts.sumOf { it.defaultVariant?.sellingPricePerUnitInPaisa ?: 0 }

        var maxCutUnboxGivesInPercentage: Double = 0.0
        var minCutUnboxGivesInPercentage: Double = 0.0
        var totalCutUnboxGivesInPaisa: Long = 0

        unboxProducts.map {
            val unboxGivesCommissionPercentage = it.defaultVariant?.unboxGivesCommissionPercentage ?: 0.0
            if (unboxGivesCommissionPercentage > maxCutUnboxGivesInPercentage) {
                maxCutUnboxGivesInPercentage = unboxGivesCommissionPercentage
            }
            if (unboxGivesCommissionPercentage < minCutUnboxGivesInPercentage) {
                minCutUnboxGivesInPercentage = unboxGivesCommissionPercentage
            }
            val commissionPaisaFromPercentage = it.defaultVariant?.sellingPricePerUnitInPaisa?.times(unboxGivesCommissionPercentage)?.div(100)?.times(100)?.toLong() ?: 0
            val cutUnboxGivesInPaisa = if (commissionPaisaFromPercentage > (it.defaultVariant?.unboxGivesMaxCommissionInPaisa ?: 0)) {
                it.defaultVariant?.unboxGivesMaxCommissionInPaisa ?: 0
            } else {
                commissionPaisaFromPercentage
            }
            totalCutUnboxGivesInPaisa += cutUnboxGivesInPaisa
            unboxProductsResponse.add(UnboxSingleProductCommissionResponse(
                product = it.toSaveProductV3Response(),
                cutUnboxGivesInPaisa = cutUnboxGivesInPaisa,
            ))
        }

        val totalEffectiveEarningInPaisa = ownTotalAmountOfProductsInPaisa + totalCutUnboxGivesInPaisa - totalCutUnboxTakesInPaisa

        return CommissionDetailsForTaggedProductsResponse (
            totalEffectiveEarningInPaisa = totalEffectiveEarningInPaisa,
            ownInventory = OwnInventoryProductsCommissionDetails (
                ownProducts = ownProductsResponse,
                totalAmountOfProductsInPaisa = ownTotalAmountOfProductsInPaisa,
                maxCutUnboxTakesInPercentage = maxCutUnboxTakesInPercentage,
                minCutUnboxTakesInPercentage = minCutUnboxTakesInPercentage,
                totalCutUnboxTakesInPaisa = totalCutUnboxTakesInPaisa,
            ),
            unboxInventory = UnboxInventoryProductsCommissionDetails(
                unboxProducts = unboxProductsResponse,
                totalAmountOfProductsInPaisa = unboxTotalAmountOfProductsInPaisa,
                maxCutUnboxGivesInPercentage = maxCutUnboxGivesInPercentage,
                minCutUnboxGivesInPercentage = minCutUnboxGivesInPercentage,
                totalCutUnboxGivesInPaisa = totalCutUnboxGivesInPaisa,
            )
        )
    }

}
