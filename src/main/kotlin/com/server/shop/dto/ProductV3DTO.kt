package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.UserV2PublicMiniDataResponse
import com.server.common.model.MediaDetailsV2
import com.server.shop.entities.*
import com.server.shop.enums.*
import com.server.shop.model.SpecificationInfoList
import com.server.shop.model.VariantInfoV3List
import com.server.shop.model.VariantProperties
import com.server.shop.pagination.SQLPaginationRequest
import com.server.shop.pagination.SQLPaginationResponse
import com.server.shop.pagination.SQLSlice

@JsonIgnoreProperties(ignoreUnknown = true)
data class PostTaggedProductsRequest (
    val postId: String,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductsByCreatorRequest (
    val creatorUserId: String,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeaturedProductsRequest (
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class DecorProductsRequest (
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class LightsProductsRequest (
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnboxManagedProductsRequest (
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeaturedProductsForCategoryRequest (
    val category: ProductCategoryV3,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecentSearchedProductsForCategoryRequest (
    val category: ProductCategoryV3,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class TrendyOfferProductsForCategoryRequest (
    val category: ProductCategoryV3,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductsForCategoryRequest (
    val category: ProductCategoryV3,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductsForSubCategoryRequest (
    val subCategory: ProductSubCategory,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProductsForVerticalRequest (
    val vertical: ProductVertical,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimilarProductsRequest (
    val productVariantId: String,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

@JsonIgnoreProperties(ignoreUnknown = true)
data class BookmarkProductVariantV3Request (
    val productVariantId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BookmarkedProductVariantV3Response (
    val productVariant: SavedProductVariantV3Response,
    val bookmarked: Boolean,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class AllBookmarkedProductVariantsRequest (
    val userId: String,
    override val page: Int,
    override val limit: Int,
): SQLPaginationRequest()

data class VariantAndProductResponse (
    val variant: SavedProductVariantV3Response,
    val product: SavedProductV3Response,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllBookmarkedProductVariantsResponse (
    val variantAndProductList: List<VariantAndProductResponse>,
    override val askedForPage: Int,
    override val askedForLimit: Int,
    override val hasNext: Boolean,
    override val nextPage: Int,
    override val numFound: Int,
): SQLPaginationResponse(
    askedForPage = askedForPage,
    askedForLimit = askedForLimit,
    hasNext = hasNext,
    nextPage = nextPage,
    numFound = numFound)

data class OwnSingleProductCommissionResponse (
    val product: SavedProductV3Response,
    val cutUnboxTakesInPaisa: Long,
)

data class UnboxSingleProductCommissionResponse (
    val product: SavedProductV3Response,
    val cutUnboxGivesInPaisa: Long,
)

data class OwnInventoryProductsCommissionDetails (
    val ownProducts: List<OwnSingleProductCommissionResponse>,
    val totalAmountOfProductsInPaisa: Long,

    val maxCutUnboxTakesInPercentage: Double,
    val minCutUnboxTakesInPercentage: Double,
    val totalCutUnboxTakesInPaisa: Long,
)

data class UnboxInventoryProductsCommissionDetails (
    val unboxProducts: List<UnboxSingleProductCommissionResponse>,
    val totalAmountOfProductsInPaisa: Long,

    val maxCutUnboxGivesInPercentage: Double,
    val minCutUnboxGivesInPercentage: Double,
    val totalCutUnboxGivesInPaisa: Long,
)

data class CommissionDetailsForTaggedProductsResponse (
    val totalEffectiveEarningInPaisa: Long,

    val ownInventory: OwnInventoryProductsCommissionDetails,
    val unboxInventory: UnboxInventoryProductsCommissionDetails,
)

data class CommissionDetailsForTaggedProductsRequest (
    val taggedProductsIds: Set<String>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveProductV3Request (

    val brandId: String? = null,

    val productUnit: ProductUnitV3,

    // Step 1
    // Handle in Variants

    // Step 2
    // Take a single or set of categories and then add sub-categories and verticals yourself using internal category team.
    val allProductCategories: AllProductCategories,

    // Step 3
    // Handle in Variants
    val allProductVariants: List<SaveProductVariantV3Request> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveProductVariantV3Request (
    val title: String,
    val description: String?,
    val mediaDetails: MediaDetailsV2,

    val viewInRoomAllowed: Boolean = false,


    // Location of the user or the post right now, but we can also take this as user input
    val locationId: String?,


    val mrpInRupees: Double,
    val sellingPriceInRupees: Double,
    // Even if there is no variant handling in UI, assume that we will have variants
    val totalUnitInStock: Long = -1,

    val deliversOverIndia: Boolean = true,

    // If limited then use distance
    val maxDeliveryDistanceInKm: Int = 300,

    val replacementAcceptable: Boolean = true,
    val returnAcceptable: Boolean = true,
    val codAvailable: Boolean = true,
    val minOrderUnitCount: Long = 1,
    val maxOrderPerUser: Long = -1, // -1 -> Unlimited, 2 -> 2 orders per user

    val allVariantInfos: VariantInfoV3List = VariantInfoV3List(emptyMap()),
    val allProductProperties: VariantProperties = VariantProperties(emptyList()),
    val specificationInfoList: SpecificationInfoList = SpecificationInfoList(emptyList()),
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductVariantV3Response (
    val id: String,
    val productId: String,
    val title: String,
    val description: String?,
    val mediaDetails: MediaDetailsV2,

    val unboxTakesCommissionPercentage: Double,
    val unboxTakesMaxCommissionInPaisa: Long,

    val unboxGivesCommissionPercentage: Double,
    val unboxGivesMaxCommissionInPaisa: Long,
    val managedByUnbox: Boolean,

    val variantInfos: VariantInfoV3List,

    val properties: VariantProperties,

    val specification: SpecificationInfoList,

    val status: ProductVariantStatusV3,

    val viewInRoomAllowed: Boolean,

    val categories: AllProductCategoriesResponse,

    val shippedFrom: SavedAddressV3Response?,
    val maxDeliveryDistanceInKm: Int,
    val deliversOverIndia: Boolean,

    val maxDeliveryTimeInSeconds: Long?,

    val replacementAcceptable: Boolean,
    val returnAcceptable: Boolean,

    val codAvailable: Boolean,

    val mrpPerUnitInPaisa: Long,
    val sellingPricePerUnitInPaisa: Long,

    val minOrderUnitCount: Long,
    val maxOrderPerUser: Long,


    val totalUnitInStock: Long,
    val totalSoldUnits: Long,
    val totalSoldAmountInPaisa: Long,
    val totalOrdersCount: Long?,

)


@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedProductV3Response (
    val id: String,

    val byUserId: String,
    val companyId: String? = null,
    val brandId: String? = null,

    val productUnit: ProductUnitV3,

    val allProductCategories: AllProductCategoriesResponse,

    val defaultVariant: SavedProductVariantV3Response,

    val brand: SavedBrandResponse? = null,

    val addedBy: UserV2PublicMiniDataResponse,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllSavedProductV3Response (
    val products: List<SavedProductV3Response>,
    override val askedForPage: Int,
    override val askedForLimit: Int,
    override val hasNext: Boolean,
    override val nextPage: Int,
    override val numFound: Int,
): SQLPaginationResponse(
    askedForPage = askedForPage,
    askedForLimit = askedForLimit,
    hasNext = hasNext,
    nextPage = nextPage,
    numFound = numFound)

fun ProductV3.toSaveProductV3Response(): SavedProductV3Response {
    this.apply {
        return SavedProductV3Response(
            id = id,
            byUserId = addedBy!!.id,
            companyId = company?.id,
            brandId = brand?.id,

            productUnit = productUnit,

            allProductCategories = getAllProductCategories().toAllProductCategoriesResponse(),

            defaultVariant = defaultVariant!!.toSaveProductVariantV3Response(),

            brand = brand?.toSavedBrandResponse(),

            addedBy = addedBy!!.toUserV2PublicMiniDataResponse(),
        )
    }
}

fun ProductVariantV3.toSaveProductVariantV3Response(): SavedProductVariantV3Response {
    this.apply {
        return SavedProductVariantV3Response(
            id = id,
            productId = product!!.id,
            title = title,
            description = description,
            mediaDetails = getMediaDetailsV2(),

            unboxTakesCommissionPercentage = unboxTakesCommissionPercentage,
            unboxTakesMaxCommissionInPaisa = unboxTakesMaxCommissionInPaisa,

            unboxGivesCommissionPercentage = unboxGivesCommissionPercentage,
            unboxGivesMaxCommissionInPaisa = unboxGivesMaxCommissionInPaisa,
            managedByUnbox = managedByUnbox,

            variantInfos = getVariantInfoV3List(),

            properties = getVariantProperties(),

            specification = getSpecificationInfoList(),

            status = status,

            viewInRoomAllowed = viewInRoomAllowed,

            categories = getAllProductCategories().toAllProductCategoriesResponse(),

            shippedFrom = shippedFrom?.toSavedAddressV3Response(),
            maxDeliveryDistanceInKm = maxDeliveryDistanceInKm,
            deliversOverIndia = deliversOverIndia,

            maxDeliveryTimeInSeconds = maxDeliveryTimeInSeconds,

            replacementAcceptable = replacementAcceptable,
            returnAcceptable = returnAcceptable,

            codAvailable = codAvailable,

            mrpPerUnitInPaisa = mrpPerUnitInPaisa,
            sellingPricePerUnitInPaisa = sellingPricePerUnitInPaisa,

            minOrderUnitCount = minOrderUnitCount,
            maxOrderPerUser = maxOrderPerUser,


            totalUnitInStock = totalUnitInStock,
            totalSoldUnits = totalSoldUnits,
            totalSoldAmountInPaisa = totalSoldAmountInPaisa,
            totalOrdersCount = totalOrdersCount,

        )
    }
}

fun SQLSlice<BookmarkedProductsV3>.toAllBookmarkedProductVariantsResponse(): AllBookmarkedProductVariantsResponse {
    this.apply {
        return AllBookmarkedProductVariantsResponse(
            variantAndProductList = content.filter { it?.productVariant != null && it?.product != null } .map { VariantAndProductResponse(
                variant = it!!.productVariant!!.toSaveProductVariantV3Response(),
                product = it!!.product!!.toSaveProductV3Response(),
            ) },
            askedForPage = askedForPage,
            askedForLimit = askedForLimit,
            nextPage = nextPage,
            numFound = numFound,
            hasNext = hasNext
        )
    }
}
