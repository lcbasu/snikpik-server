package com.server.dk.service

import com.server.dk.dto.*

abstract class ProductService {
    abstract fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse?
    abstract fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse?
    abstract fun getAllProducts(companyId: String): AllProductsResponse
    abstract fun getAllProductUnits(): AllProductUnits
    abstract fun updateStatus(updateProductStatusRequest: UpdateProductStatusRequest): SavedProductResponse?
    abstract fun updateProduct(request: UpdateProductRequest): SavedProductResponse?
}
