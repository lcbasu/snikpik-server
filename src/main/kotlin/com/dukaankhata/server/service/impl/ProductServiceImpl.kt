package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.ProductService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CollectionUtils
import com.dukaankhata.server.utils.ProductUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl : ProductService() {
    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var productUtils: ProductUtils

    @Autowired
    private lateinit var collectionUtils: CollectionUtils

    override fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveProductRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedProduct = productUtils.saveProduct(company, requestContext.user, saveProductRequest) ?: error("Error while saving product")
        return savedProduct.toSavedProductResponse()
    }

    override fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = addProductsToCollectionRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val collection = collectionUtils.getCollection(addProductsToCollectionRequest.collectionId) ?: error("Collection is required")
        val savedProductsCollection = productUtils.addProductsToCollection(company, requestContext.user, collection, addProductsToCollectionRequest)
        return AddProductsToCollectionResponse(
            company = company.toSavedCompanyResponse(),
            collection = collection.toSavedCollectionResponse(),
            products = savedProductsCollection.map { it.product!!.toSavedProductResponse() }
        )
    }
}
