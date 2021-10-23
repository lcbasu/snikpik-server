package com.server.dk.service.impl

import com.server.common.provider.AuthProvider
import com.server.dk.dto.*
import com.server.dk.enums.ProductUnit
import com.server.dk.enums.toProductUnitResponse
import com.server.dk.provider.ProductCollectionProvider
import com.server.dk.provider.ProductProvider
import com.server.dk.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl : ProductService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    override fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveProductRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedProduct = productProvider.saveProduct(company, requestContext.user, saveProductRequest) ?: error("Error while saving product")
        return savedProduct.toSavedProductResponse()
    }

    override fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = addProductsToCollectionRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        return productCollectionProvider.addProductsToCollection(company, requestContext.user, addProductsToCollectionRequest)
    }

    override fun getAllProducts(companyId: String): AllProductsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyId
        )
        val company = requestContext.company ?: error("Company is required")
        return productProvider.getAllProducts(company)
    }

    override fun getAllProductUnits(): AllProductUnits {
        return AllProductUnits(
            productUnits = ProductUnit.values().map {
                it.toProductUnitResponse()
            }
        )
    }

    override fun updateStatus(request: UpdateProductStatusRequest): SavedProductResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = request.companyServerIdOrUsername,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val updatedProduct = productProvider.updateStatus(company, request) ?: error("Error while update product status")
        return updatedProduct.toSavedProductResponse()
    }

    override fun updateProduct(request: UpdateProductRequest): SavedProductResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = request.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val updatedProduct = productProvider.updateProduct(company, requestContext.user, request) ?: error("Error while updating product")
        return updatedProduct.toSavedProductResponse()
    }
}
