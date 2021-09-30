package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class CustomerService {
    abstract fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse
    abstract fun getRelatedProducts(productId: String): RelatedProductsResponse?
    abstract fun updateCart(updateCartRequest: UpdateCartRequest): SavedProductOrderResponse?
    abstract fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse?
    abstract fun getActiveDiscounts(companyId: String): SavedActiveDiscountsResponse
    abstract fun migrateCart(migrateCartRequest: MigrateCartRequest): MigratedProductOrderResponse
    abstract fun getProductOrder(productOrderId: String): SavedProductOrderResponse
    abstract fun getProductDetails(productId: String): SavedProductResponse
    abstract fun getProductOrders(): AllProductOrdersResponse
    abstract fun getCollectionWithProducts(collectionId: String): CollectionWithProductsResponse
}
