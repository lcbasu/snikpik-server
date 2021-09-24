package com.dukaankhata.server.provider

import com.dukaankhata.server.dto.ShopViewForCustomerResponse
import com.dukaankhata.server.dto.toAllCollectionsWithProductsResponse
import com.dukaankhata.server.dto.toSavedCompanyResponse
import com.dukaankhata.server.dto.toSavedUserResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.User
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CustomerProvider {

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    fun getShopViewForCustomer(user: User, company: Company): ShopViewForCustomerResponse  {
        return runBlocking {
            val allCollectionWithProducts = collectionProvider.getAllCollectionWithProductsRaw(company)

            val allProducts = allCollectionWithProducts.collectionsWithProducts.map { it.products }.flatten()
            val allCollections = allCollectionWithProducts.collectionsWithProducts.map { it.collection }

            val allCartItemsFuture = async { cartItemProvider.getCartItemsForUserForProducts(user.id, allProducts.map { it.id }.toSet()) }
            val bestsellerProducts = async { productProvider.getBestSellerProducts(allProducts) }
            val bestsellerCollections = async { collectionProvider.getBestSellerCollections(allCollections) }

            val allCartItems = allCartItemsFuture.await()
            val productsOrderedInPast = productProvider.getProductsOrderedInPast(allCartItems)

            val productCollectionsOrderedFromInPast = productCollectionProvider.getProductCollections(
                collectionIds = emptySet(),
                productIds = productsOrderedInPast.map { it.id }.toSet()
            )

            ShopViewForCustomerResponse(
                user = user.toSavedUserResponse(),
                company = company.toSavedCompanyResponse(),
                allCollectionsWithProducts = allCollectionWithProducts.toAllCollectionsWithProductsResponse(productVariantProvider, productCollectionProvider),
                bestsellerProductsIds = bestsellerProducts.await().map { it.id }.toSet(),
                pastOrderedProductsIds = productsOrderedInPast.map { it.id }.toSet(),
                bestsellerCollectionsIds = bestsellerCollections.await().map { it.id }.toSet(),
                collectionsIdsOrderedFromInPast = productCollectionsOrderedFromInPast.mapNotNull { it.collection }.map { it.id }.toSet()
            )
        }
    }
}