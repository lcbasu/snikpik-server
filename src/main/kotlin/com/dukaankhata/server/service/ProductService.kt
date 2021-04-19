package com.dukaankhata.server.service

import com.dukaankhata.server.dto.AddProductsToCollectionRequest
import com.dukaankhata.server.dto.AddProductsToCollectionResponse
import com.dukaankhata.server.dto.SaveProductRequest
import com.dukaankhata.server.dto.SavedProductResponse

abstract class ProductService {
    abstract fun saveProduct(saveProductRequest: SaveProductRequest): SavedProductResponse?
    abstract fun addProductsToCollection(addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse?
}
