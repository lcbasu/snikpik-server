package com.server.shop.service

import com.server.shop.dto.*
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductCategoryV3Group
import com.server.shop.enums.ProductSubCategory

abstract class ProductV3Service {
    abstract fun saveProduct(request: SaveProductV3Request): SavedProductV3Response?

    abstract fun getAllProductCategories(categoryGroup: ProductCategoryV3Group): AllProductCategoriesResponse

    abstract fun getAllProductSubCategoriesForCategory(category: ProductCategoryV3): AllProductSubCategoriesResponse

    abstract fun getAllProductVerticalsForSubCategory(subCategory: ProductSubCategory): AllProductVerticalsResponse
    abstract fun getAllProductVerticalsForCategory(category: ProductCategoryV3): AllProductVerticalsResponse

    abstract fun getCommissionDetailsForTaggedProducts(request: CommissionDetailsForTaggedProductsRequest): CommissionDetailsForTaggedProductsResponse

    abstract fun updateBookmarkProductVariant(request: BookmarkProductVariantV3Request): BookmarkedProductVariantV3Response?
    abstract fun getAllBookmarkedProductVariants(request: AllBookmarkedProductVariantsRequest): AllBookmarkedProductVariantsResponse
    abstract fun getIsProductVariantBookmarked(productVariantId: String): BookmarkedProductVariantV3Response?

    abstract fun getSimilarProducts(request: SimilarProductsRequest): AllSavedProductV3Response
    abstract fun getFeaturedProductsForCategory(request: FeaturedProductsForCategoryRequest): AllSavedProductV3Response
    abstract fun getRecentSearchedProductsForCategory(request: RecentSearchedProductsForCategoryRequest): AllSavedProductV3Response
    abstract fun getTrendyOfferProductsForCategory(request: TrendyOfferProductsForCategoryRequest): AllSavedProductV3Response
    abstract fun getAllProductsForCategory(request: AllProductsForCategoryRequest): AllSavedProductV3Response
    abstract fun getAllProductsForSubCategory(request: AllProductsForSubCategoryRequest): AllSavedProductV3Response
    abstract fun getAllProductsForVertical(request: AllProductsForVerticalRequest): AllSavedProductV3Response
    abstract fun getFeaturedProducts(request: FeaturedProductsRequest): AllSavedProductV3Response
    abstract fun getDecorProducts(request: DecorProductsRequest): AllSavedProductV3Response
    abstract fun getUnboxManagedProducts(request: UnboxManagedProductsRequest): AllSavedProductV3Response
    abstract fun getProductsByCreator(request: ProductsByCreatorRequest): AllSavedProductV3Response
    abstract fun getPostTaggedProducts(request: PostTaggedProductsRequest): AllSavedProductV3Response
    abstract fun getLightsProducts(request: LightsProductsRequest): AllSavedProductV3Response

}
