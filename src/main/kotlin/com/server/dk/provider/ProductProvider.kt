package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.ProductRepository
import com.server.dk.dto.*
import com.server.dk.entities.*
import com.server.dk.enums.ProductStatus
import com.server.common.enums.ReadableIdPrefix
import com.server.dk.model.convertToString
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductProvider {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

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

            // Add product to existing collections
            if (saveProductRequest.collectionsIds.isNotEmpty()) {
                productCollectionProvider.addProductToCollections(
                    company,
                    user,
                    AddProductToCollectionsRequest(
                        companyId = company.id,
                        collectionsIds = saveProductRequest.collectionsIds,
                        productId = savedProduct.id
                    )
                )
            }
            return savedProduct
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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
        val currentProductCollection = productCollectionProvider.getProductCollections(collectionIds = emptySet(), productIds = setOf(productId))
        val allCollectionsIds = currentProductCollection.mapNotNull { it.collection }.map { it.id }.toSet()
        val allProductCollections = productCollectionProvider.getProductCollections(collectionIds = allCollectionsIds, productIds = emptySet())
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
                    async { it.toSavedProductResponse() }
                }.map {
                    it.await()
                }
            )
        }
    }

    fun increaseClick(savedEntityTracking: EntityTracking) {
        val product = savedEntityTracking.product ?: return
        try {
            product.totalClicksCount = (product.totalClicksCount ?: 0) + 1
            productRepository.save(product)
            companyProvider.increaseCompanyProductClick(savedEntityTracking)
            collectionProvider.increaseProductCollectionsClick(savedEntityTracking)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseView(savedEntityTracking: EntityTracking) {
        val product = savedEntityTracking.product ?: return
        try {
            product.totalViewsCount = (product.totalViewsCount ?: 0) + 1
            productRepository.save(product)
            companyProvider.increaseCompanyProductView(savedEntityTracking)
            collectionProvider.increaseProductCollectionsView(savedEntityTracking)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseProductVariantClick(savedEntityTracking: EntityTracking) {
        val productVariant = savedEntityTracking.productVariant ?: return
        val product = productVariant.product ?: return
        try {
            product.totalVariantsClicksCount = (product.totalVariantsClicksCount ?: 0) + 1
            productRepository.save(product)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun increaseProductVariantView(savedEntityTracking: EntityTracking) {
        val productVariant = savedEntityTracking.productVariant ?: return
        val product = productVariant.product ?: return
        try {
            product.totalVariantsViewsCount = (product.totalVariantsViewsCount ?: 0) + 1
            productRepository.save(product)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateOrderDetails(productOrder: ProductOrder) {
        val products = productOrder.cartItems.mapNotNull { it.product }
        try {
            val updatedProducts = products.map {
                val prod = it
                prod.totalOrdersCount = (prod.totalOrdersCount ?: 0) + 1
                prod.totalOrderAmountInPaisa = (prod.totalOrderAmountInPaisa ?: 0) + productOrder.totalPricePayableInPaisa
                prod.totalUnitsOrdersCount = (prod.totalUnitsOrdersCount ?: 0) + productOrder.cartItems.sumBy { it.totalUnits.toInt() }
                prod
            }
            productRepository.saveAll(updatedProducts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
