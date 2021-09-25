package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.ProductCollectionRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.entities.Collection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductCollectionProvider {

    @Autowired
    private lateinit var productCollectionRepository: ProductCollectionRepository

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Transactional
    fun addProductsToCollection(company: Company, user: User, addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse {
        val collection = collectionProvider.getCollection(addProductsToCollectionRequest.collectionId) ?: error("Collection is required")
        val products = productProvider.getProducts(addProductsToCollectionRequest.productIds)
        val savedProductsCollections = products.filterNotNull().map {
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
        return AddProductsToCollectionResponse(
            company = company.toSavedCompanyResponse(),
            collection = collection.toSavedCollectionResponse(),
            products = savedProductsCollections.map { it.product!!.toSavedProductResponse() }
        )
    }

    @Transactional
    fun addProductToCollections(company: Company, user: User, addProductToCollectionsRequest: AddProductToCollectionsRequest): AddProductToCollectionsResponse {
        val collections = collectionProvider.getCollections(addProductToCollectionsRequest.collectionsIds).filterNotNull()
        val product = productProvider.getProduct(addProductToCollectionsRequest.productId) ?: error("No product found for id: ${addProductToCollectionsRequest.productId}")
        val savedProductsCollections = collections.map {
            val productCollection = ProductCollection()
            val productCollectionKey = ProductCollectionKey()
            productCollectionKey.collectionId = it.id
            productCollectionKey.productId = product.id

            productCollection.id = productCollectionKey
            productCollection.product = product
            productCollection.collection = it
            productCollection.company = company
            productCollection.addedBy = user
            productCollectionRepository.save(productCollection)
        }
        return AddProductToCollectionsResponse(
            company = company.toSavedCompanyResponse(),
            collections = collections.map { it.toSavedCollectionResponse() },
            product = product.toSavedProductResponse()
        )
    }

    fun getProductCollections(collectionIds: Set<String> = emptySet(), productIds: Set<String> = emptySet()): List<ProductCollection> =
        try {
            productCollectionRepository.getProductCollections(productIds = productIds, collectionIds = collectionIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getAllCollectionsForProduct(product: Product): List<Collection> =
        try {
            getProductCollections(productIds = setOf(product.id)).mapNotNull { it.collection }
        } catch (e: Exception) {
            emptyList()
        }
}
