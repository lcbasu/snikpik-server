package com.server.dk.service.impl

import com.server.common.provider.AuthProvider
import com.server.dk.dto.SavedProductResponse
import com.server.dk.dto.ShopViewForCustomerResponse
import com.server.dk.dto.toSavedProductResponse
import com.server.dk.provider.*
import com.server.dk.service.SEODataService
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
