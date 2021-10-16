package com.server.dk.service

import com.server.dk.dto.SavedProductResponse
import com.server.dk.dto.ShopViewForCustomerResponse

abstract class SEODataService {
    abstract fun getProductDetails(productId: String): SavedProductResponse
    abstract fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse?
}
