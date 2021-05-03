package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

// For anything related to DKShop, we interact with
// this service
abstract class DKShopService {
    abstract fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse?
    abstract fun isUsernameAvailable(username: String): UsernameAvailableResponse?
    abstract fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse?
    abstract fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse?
    abstract fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse
    abstract fun getRelatedProducts(productId: String): RelatedProductsResponse?
    abstract fun updateCart(updateCartRequest: UpdateCartRequest): SavedProductOrderResponse?
    abstract fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse?
    abstract fun getActiveDiscounts(companyId: String): SavedActiveDiscountsResponse
    abstract fun saveOrUpdateExtraChargeDelivery(saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest): SavedExtraChargeDeliveryResponse
    abstract fun getExtraCharges(companyId: String): SavedExtraChargesResponse
    abstract fun saveOrUpdateExtraChargeTax(saveExtraChargeTaxRequest: SaveExtraChargeTaxRequest): SavedExtraChargeTaxResponse
    abstract fun migrateCart(migrateCartRequest: MigrateCartRequest): MigratedProductOrderResponse
    abstract fun getProductOrder(productOrderId: String): SavedProductOrderResponse
}
