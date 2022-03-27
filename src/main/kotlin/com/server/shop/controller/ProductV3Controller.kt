package com.server.shop.controller

import com.server.shop.dto.*
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductCategoryV3Group
import com.server.shop.enums.ProductSubCategory
import com.server.shop.enums.ProductVertical
import com.server.shop.service.ProductV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("shop/product/v3")
class ProductV3Controller {

    @Autowired
    private lateinit var productV3Service: ProductV3Service

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveProduct(@RequestBody request: SaveProductV3Request): SavedProductV3Response? {
        return productV3Service.saveProduct(request)
    }

    @RequestMapping(value = ["/getAllProductCategories"], method = [RequestMethod.GET])
    fun getAllProductCategories(@RequestParam categoryGroup: ProductCategoryV3Group): AllProductCategoriesResponse {
        return productV3Service.getAllProductCategories(categoryGroup)
    }

    @RequestMapping(value = ["/getAllProductSubCategoriesForCategory"], method = [RequestMethod.GET])
    fun getAllProductSubCategoriesForCategory(@RequestParam category: ProductCategoryV3): AllProductSubCategoriesResponse {
        return productV3Service.getAllProductSubCategoriesForCategory(category)
    }

    @RequestMapping(value = ["/getAllProductVerticalsForSubCategory"], method = [RequestMethod.GET])
    fun getAllProductVerticalsForSubCategory(@RequestParam subCategory: ProductSubCategory): AllProductVerticalsResponse {
        return productV3Service.getAllProductVerticalsForSubCategory(subCategory)
    }

    @RequestMapping(value = ["/getAllProductVerticalsForCategory"], method = [RequestMethod.GET])
    fun getAllProductVerticalsForCategory(@RequestParam category: ProductCategoryV3): AllProductVerticalsResponse {
        return productV3Service.getAllProductVerticalsForCategory(category)
    }

    @RequestMapping(value = ["/getFeaturedProducts"], method = [RequestMethod.GET])
    fun getFeaturedProducts(@RequestParam page: Int,
                            @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getFeaturedProducts(FeaturedProductsRequest(
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getDecorProducts"], method = [RequestMethod.GET])
    fun getDecorProducts(@RequestParam page: Int,
                         @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getDecorProducts(DecorProductsRequest(page, limit))
    }

    @RequestMapping(value = ["/getLightsProducts"], method = [RequestMethod.GET])
    fun getLightsProducts(@RequestParam page: Int,
                          @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getLightsProducts(LightsProductsRequest(page, limit))
    }

    @RequestMapping(value = ["/getUnboxManagedProducts"], method = [RequestMethod.GET])
    fun getUnboxManagedProducts(@RequestParam page: Int,
                                @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getUnboxManagedProducts(UnboxManagedProductsRequest(page, limit))
    }

    @RequestMapping(value = ["/getFeaturedProductsForCategory"], method = [RequestMethod.GET])
    fun getFeaturedProductsForCategory(@RequestParam category: ProductCategoryV3,
                                       @RequestParam page: Int,
                                       @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getFeaturedProductsForCategory(FeaturedProductsForCategoryRequest(
            category,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getRecentSearchedProductsForCategory"], method = [RequestMethod.GET])
    fun getRecentSearchedProductsForCategory(@RequestParam category: ProductCategoryV3,
                                             @RequestParam page: Int,
                                             @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getRecentSearchedProductsForCategory(RecentSearchedProductsForCategoryRequest(
            category,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getTrendyOfferProductsForCategory"], method = [RequestMethod.GET])
    fun getTrendyOfferProductsForCategory(@RequestParam category: ProductCategoryV3,
                                          @RequestParam page: Int,
                                          @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getTrendyOfferProductsForCategory(TrendyOfferProductsForCategoryRequest(
            category,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getAllProductsForCategory"], method = [RequestMethod.GET])
    fun getAllProductsForCategory(@RequestParam category: ProductCategoryV3,
                                  @RequestParam page: Int,
                                  @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getAllProductsForCategory(AllProductsForCategoryRequest(
            category,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getAllProductsForSubCategory"], method = [RequestMethod.GET])
    fun getAllProductsForSubCategory(@RequestParam subCategory: ProductSubCategory,
                                     @RequestParam page: Int,
                                     @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getAllProductsForSubCategory(AllProductsForSubCategoryRequest(
            subCategory,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getAllProductsForVertical"], method = [RequestMethod.GET])
    fun getAllProductsForVertical(@RequestParam vertical: ProductVertical,
                                  @RequestParam page: Int,
                                  @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getAllProductsForVertical(AllProductsForVerticalRequest(
            vertical,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getSimilarProducts"], method = [RequestMethod.GET])
    fun getSimilarProducts(@RequestParam productVariantId: String,
                           @RequestParam page: Int,
                           @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getSimilarProducts(SimilarProductsRequest(
            productVariantId,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/getProductsByCreator"], method = [RequestMethod.GET])
    fun getProductsByCreator(@RequestParam creatorUserId: String,
                             @RequestParam page: Int,
                             @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getProductsByCreator(ProductsByCreatorRequest(
            creatorUserId,
            page,
            limit,
        ))
    }


    @RequestMapping(value = ["/getCommissionDetailsForTaggedProducts"], method = [RequestMethod.GET])
    fun getCommissionDetailsForTaggedProducts(@RequestParam taggedProductsIds: String): CommissionDetailsForTaggedProductsResponse {
        return productV3Service.getCommissionDetailsForTaggedProducts(CommissionDetailsForTaggedProductsRequest(
            taggedProductsIds.split(",").toSet(),
        ))
    }

    @RequestMapping(value = ["/getPostTaggedProducts"], method = [RequestMethod.GET])
    fun getPostTaggedProducts(@RequestParam postId: String,
                              @RequestParam page: Int,
                              @RequestParam limit: Int,): AllSavedProductV3Response {
        return productV3Service.getPostTaggedProducts(PostTaggedProductsRequest(
            postId,
            page,
            limit,
        ))
    }

    @RequestMapping(value = ["/updateBookmarkProductVariant"], method = [RequestMethod.POST])
    fun updateBookmarkProductVariant(@RequestBody request: BookmarkProductVariantV3Request): BookmarkedProductVariantV3Response? {
        return productV3Service.updateBookmarkProductVariant(request)
    }

    @RequestMapping(value = ["/getIsProductVariantBookmarked"], method = [RequestMethod.GET])
    fun getIsProductVariantBookmarked(@RequestParam productVariantId: String): BookmarkedProductVariantV3Response? {
        return productV3Service.getIsProductVariantBookmarked(productVariantId)
    }

    @RequestMapping(value = ["/getAllBookmarkedProductVariants"], method = [RequestMethod.GET])
    fun getAllBookmarkedProductVariants(@RequestParam userId: String,
                                        @RequestParam page: Int,
                                        @RequestParam limit: Int,): AllBookmarkedProductVariantsResponse {
        return productV3Service.getAllBookmarkedProductVariants(AllBookmarkedProductVariantsRequest(
            userId = userId,
            page = page,
            limit = limit,
        ))
    }

}
