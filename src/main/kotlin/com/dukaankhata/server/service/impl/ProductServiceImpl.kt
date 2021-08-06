package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.ProductUnit
import com.dukaankhata.server.service.ProductService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CollectionProvider
import com.dukaankhata.server.provider.ProductProvider
import com.dukaankhata.server.provider.ProductVariantProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl : ProductService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    override fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = saveProductRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedProduct = productProvider.saveProduct(company, requestContext.user, saveProductRequest) ?: error("Error while saving product")
        return savedProduct.toSavedProductResponse(productVariantProvider)
    }

    override fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = addProductsToCollectionRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val collection = collectionProvider.getCollection(addProductsToCollectionRequest.collectionId) ?: error("Collection is required")
        val savedProductsCollection = productProvider.addProductsToCollection(company, requestContext.user, collection, addProductsToCollectionRequest)
        return AddProductsToCollectionResponse(
            company = company.toSavedCompanyResponse(),
            collection = collection.toSavedCollectionResponse(),
            products = savedProductsCollection.map { it.product!!.toSavedProductResponse(productVariantProvider) }
        )
    }

    override fun getAllProducts(companyId: String): AllProductsResponse {
        val requestContext = authProvider.validateRequest(
            companyId = companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        return productProvider.getAllProducts(company)
    }

    override fun getAllProductUnits(): AllProductUnits {
        return AllProductUnits(
            productUnits = ProductUnit.values().map {
                ProductUnitResponse(
                    unitType = it,
                    rank = it.rank,
                    displayName = it.displayName
                )
            }
        )
    }
}
