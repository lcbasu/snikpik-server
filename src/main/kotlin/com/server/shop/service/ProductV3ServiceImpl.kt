package com.server.shop.service

import com.server.shop.dto.*
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductCategoryV3Group
import com.server.shop.enums.ProductSubCategory
import com.server.shop.enums.ProductVertical
import com.server.shop.pagination.toAllSavedProductV3Response
import com.server.shop.provider.ProductV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductV3ServiceImpl : ProductV3Service() {

    @Autowired
    private lateinit var productV3Provider: ProductV3Provider

    override fun saveProduct(request: SaveProductV3Request): SavedProductV3Response? {
        return productV3Provider.saveProduct(request)?.toSaveProductV3Response()
    }

    override fun getAllProductCategories(categoryGroup: ProductCategoryV3Group): AllProductCategoriesResponse {
        ProductCategoryV3.values().filter { it.categoryGroup == categoryGroup }.let {
            return AllProductCategoriesResponse(it.map { it.toProductCategoryV3Response() })
        }
    }

    override fun getAllProductSubCategoriesForCategory(category: ProductCategoryV3): AllProductSubCategoriesResponse {
        return AllProductSubCategoriesResponse(ProductSubCategory.values().filter { it.categories.contains(category) }.map { it.toProductSubCategoryResponse() })
    }

    override fun getAllProductVerticalsForSubCategory(subCategory: ProductSubCategory): AllProductVerticalsResponse {
        return AllProductVerticalsResponse(ProductVertical.values().filter { it.subCategories.contains(subCategory) }.map { it.toProductVerticalResponse() })
    }

    override fun getAllProductVerticalsForCategory(category: ProductCategoryV3): AllProductVerticalsResponse {
        return AllProductVerticalsResponse(ProductVertical.values().filter {
            it.subCategories.any {
                it.categories.contains(
                    category
                )
            }
        }.map { it.toProductVerticalResponse() })
    }

    override fun updateBookmarkProductVariant(request: BookmarkProductVariantV3Request): BookmarkedProductVariantV3Response? {
        return productV3Provider.updateBookmarkProductVariant(request)
    }

    override fun getIsProductVariantBookmarked(productVariantId: String): BookmarkedProductVariantV3Response? {
        return productV3Provider.getIsProductVariantBookmarked(productVariantId)
    }

    override fun getAllBookmarkedProductVariants(request: AllBookmarkedProductVariantsRequest): AllBookmarkedProductVariantsResponse {
        return productV3Provider.getAllBookmarkedProductVariants(request)
    }

    override fun getFeaturedProducts(request: FeaturedProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getFeaturedProducts(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getDecorProducts(request: DecorProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getDecorProducts(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getUnboxManagedProducts(request: UnboxManagedProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getUnboxManagedProducts(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getProductsByCreator(request: ProductsByCreatorRequest): AllSavedProductV3Response {
        val response = productV3Provider.getProductsByCreator(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getPostTaggedProducts(request: PostTaggedProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getPostTaggedProducts(request)
        return AllSavedProductV3Response(
            products = response.content.mapNotNull { it?.product?.toSaveProductV3Response() },
            askedForPage = response.askedForPage,
            askedForLimit = response.askedForLimit,
            nextPage = response.nextPage,
            numFound = response.numFound,
            hasNext = response.hasNext
        )
    }

    override fun getCommissionDetailsForTaggedProducts(request: CommissionDetailsForTaggedProductsRequest): CommissionDetailsForTaggedProductsResponse {
        return productV3Provider.getCommissionDetailsForTaggedProducts(request)
    }

    override fun getLightsProducts(request: LightsProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getLightsProducts(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getSimilarProducts(request: SimilarProductsRequest): AllSavedProductV3Response {
        val response = productV3Provider.getSimilarProducts(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getFeaturedProductsForCategory(request: FeaturedProductsForCategoryRequest): AllSavedProductV3Response {
        val response = productV3Provider.getFeaturedProductsForCategory(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getRecentSearchedProductsForCategory(request: RecentSearchedProductsForCategoryRequest): AllSavedProductV3Response {
        val response = productV3Provider.getRecentSearchedProductsForCategory(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getTrendyOfferProductsForCategory(request: TrendyOfferProductsForCategoryRequest): AllSavedProductV3Response {
        val response = productV3Provider.getTrendyOfferProductsForCategory(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getAllProductsForCategory(request: AllProductsForCategoryRequest): AllSavedProductV3Response {
        val response = productV3Provider.getAllProductsForCategory(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getAllProductsForSubCategory(request: AllProductsForSubCategoryRequest): AllSavedProductV3Response {
        val response = productV3Provider.getAllProductsForSubCategory(request)
        return response.toAllSavedProductV3Response()
    }

    override fun getAllProductsForVertical(request: AllProductsForVerticalRequest): AllSavedProductV3Response {
        val response = productV3Provider.getAllProductsForVertical(request)
        return response.toAllSavedProductV3Response()
    }
}
