package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.ProductCollectionRepository
import com.dukaankhata.server.dao.ProductRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.enums.ProductStatus
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@Component
class ProductProvider {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var productCollectionRepository: ProductCollectionRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    fun getProducts(productIds: Set<String>): List<Product?> =
        try {
            productRepository.findAllById(productIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun saveProduct(company: Company, user: User, saveProductRequest: SaveProductRequest) : Product? {
        try {
            val newProduct = Product()
            newProduct.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PRD.name)
            newProduct.addedBy = user
            newProduct.company = company
            newProduct.title = saveProductRequest.title
            newProduct.description = saveProductRequest.description
            newProduct.mediaDetails = saveProductRequest.mediaDetails.convertToString()
            newProduct.minOrderUnitCount = saveProductRequest.minOrderUnitCount
            newProduct.pricePerUnitInPaisa = saveProductRequest.originalPricePerUnitInPaisa
            newProduct.taxPerUnitInPaisa = saveProductRequest.taxPerUnitInPaisa
            newProduct.productStatus = ProductStatus.ACTIVE
            newProduct.productUnit = saveProductRequest.productUnit
            newProduct.unitQuantity = saveProductRequest.unitQuantity
            newProduct.totalUnitInStock = saveProductRequest.totalUnitInStock
            newProduct.originalPricePerUnitInPaisa = saveProductRequest.originalPricePerUnitInPaisa
            newProduct.sellingPricePerUnitInPaisa = saveProductRequest.sellingPricePerUnitInPaisa
            val savedProduct = productRepository.save(newProduct)
            // We also need to save the variants
            // If no variant is provided then save a default one with product details
            productVariantProvider.saveProductVariant(savedProduct, saveProductRequest.allProductVariants)
            return savedProduct
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

    fun getProductCollections(collectionIds: Set<String> = emptySet(), productIds: Set<String> = emptySet()): List<ProductCollection> =
        try {
            productCollectionRepository.getProductCollections(productIds = productIds, collectionIds = collectionIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getProducts(company: Company): List<Product> =
        try {
            productRepository.findAllByCompany(company)
        } catch (e: Exception) {
            emptyList()
        }

    fun getBestSellerProducts(products: List<Product>, takeMaxSize: Int = 10): List<Product> {
        return products
            .sortedBy { it.totalOrderAmountInPaisa }
            .sortedBy { it.totalViewsCount }
            .sortedBy { it.totalUnitsOrdersCount }
            .take(takeMaxSize)
    }

    fun getProductsOrderedInPast(cartItems: List<CartItem>): List<Product> {
        return cartItems
            .filter { it.productOrder != null }
            .mapNotNull { it.productVariant?.product }
            .filter { it.productStatus == ProductStatus.ACTIVE }
    }

    // Until we have the Data Science built in
    // We can return products that are part of some collection
    // where this product is also present
    fun getRelatedProducts(productId: String): List<Product> {
        val currentProductCollection = getProductCollections(collectionIds = emptySet(), productIds = setOf(productId))
        val allCollectionsIds = currentProductCollection.mapNotNull { it.collection }.map { it.id }.toSet()
        val allProductCollections = getProductCollections(collectionIds = allCollectionsIds, productIds = emptySet())
        return allProductCollections
            .mapNotNull { it.product }
            .filterNot { it.id == productId }
    }

    fun getProduct(productId: String): Product? =
        try {
            productRepository.findById(productId).get()
        } catch (e: Exception) {
            null
        }

    fun getAllProducts(company: Company): AllProductsResponse {
        return runBlocking {
            AllProductsResponse(
                products = getProducts(company).map {
                    async { it.toSavedProductResponse(productVariantProvider) }
                }.map {
                    it.await()
                }
            )
        }
    }

}
