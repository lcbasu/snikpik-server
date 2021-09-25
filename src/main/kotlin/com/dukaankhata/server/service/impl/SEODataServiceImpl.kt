package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SavedProductResponse
import com.dukaankhata.server.dto.ShopViewForCustomerResponse
import com.dukaankhata.server.dto.toSavedProductResponse
import com.dukaankhata.server.provider.*
import com.dukaankhata.server.service.SEODataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SEODataServiceImpl : SEODataService() {

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var customerProvider: CustomerProvider

    override fun getProductDetails(productId: String): SavedProductResponse {
        val product = productProvider.getProduct(productId) ?: error("Product not found for id: $productId")
        return product.toSavedProductResponse()
    }

    override fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse? {
        return customerProvider.getShopViewForCustomer(username, authProvider.getRequestUserEntity())
    }

}
