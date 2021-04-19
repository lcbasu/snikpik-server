package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.ProductCollectionRepository
import com.dukaankhata.server.dao.ProductRepository
import com.dukaankhata.server.dto.AddProductsToCollectionRequest
import com.dukaankhata.server.dto.SaveProductRequest
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.enums.ProductStatus
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductUtils {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var productCollectionRepository: ProductCollectionRepository

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getProducts(productIds: Set<String>): List<Product?> =
        try {
            productRepository.findAllById(productIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun saveProduct(company: Company, user: User, saveProductRequest: SaveProductRequest) : Product? {
        try {
            val newProduct = Product()
            newProduct.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.PRD.getPrefix())
            newProduct.addedBy = user
            newProduct.company = company
            newProduct.title = saveProductRequest.title
            newProduct.mediaDetails = saveProductRequest.mediaDetails.convertToString()
            newProduct.minOrderUnitCount = saveProductRequest.minOrderUnitCount
            newProduct.pricePerUnitInPaisa = saveProductRequest.pricePerUnitInPaisa
            newProduct.taxPerUnitInPaisa = saveProductRequest.taxPerUnitInPaisa
            newProduct.productStatus = ProductStatus.ACTIVE
            newProduct.productUnit = saveProductRequest.productUnit
            newProduct.totalUnitInStock = saveProductRequest.totalUnitInStock
            return productRepository.save(newProduct)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Transactional
    fun addProductsToCollection(company: Company, user: User, collection: Collection, addProductsToCollectionRequest: AddProductsToCollectionRequest): List<ProductCollection> {
        val products = getProducts(addProductsToCollectionRequest.productIds)
        return products.filterNotNull().map {
            val productCollection = ProductCollection()
            val productCollectionKey = ProductCollectionKey()
            productCollectionKey.collectionId = collection.id
            productCollectionKey.productId = it.id

            productCollection.id = productCollectionKey
            productCollection.product = it
            productCollection.collection = collection
            productCollection.company = company
            productCollection.addedBy = user
            productCollectionRepository.save(productCollection)
        }
    }

}
