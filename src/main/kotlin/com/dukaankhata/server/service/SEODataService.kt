package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SavedProductResponse

abstract class SEODataService {
    abstract fun getProductDetails(productId: String): SavedProductResponse
}
