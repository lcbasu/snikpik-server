package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SavedProductResponse
import com.dukaankhata.server.dto.ShopViewForCustomerResponse

abstract class SEODataService {
    abstract fun getProductDetails(productId: String): SavedProductResponse
    abstract fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse?
}
