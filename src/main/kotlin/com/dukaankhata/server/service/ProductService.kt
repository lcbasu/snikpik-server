package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class ProductService {
    abstract fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse?
    abstract fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse?
    abstract fun getAllProducts(companyId: String): AllProductsResponse
    abstract fun getAllProductUnits(): AllProductUnits
}
