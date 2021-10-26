package com.server.dk.provider

import com.server.dk.dto.ShopViewForCustomerResponse
import com.server.dk.dto.toAllCollectionsWithProductsResponse
import com.server.dk.dto.toSavedCompanyResponse
import com.server.dk.dto.toSavedUserResponse
import com.server.common.entities.User
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
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    fun getShopViewForCustomer(shopUsername: String, user: User?): ShopViewForCustomerResponse  {
        return runBlocking {
            val company = companyProvider.getCompanyByUsername(shopUsername) ?: error("shopUsername not found $shopUsername")
            val allCollectionWithProducts = collectionProvider.getAllCollectionWithProductsRaw(company)

            val allProducts = allCollectionWithProducts.collectionsWithProducts.map { it.products }.flatten()
            val allCollections = allCollectionWithProducts.collectionsWithProducts.map { it.collection }

            val allCartItemsFuture = async { user?.let { cartItemProvider.getCartItemsForUserForProducts(it.id, allProducts.map { it.id }.toSet()) } }
            val bestsellerProducts = async { productProvider.getBestSellerProducts(allProducts) }
            val bestsellerCollections = async { collectionProvider.getBestSellerCollections(allCollections) }

            val allCartItems = allCartItemsFuture.await() ?: emptyList()
            val productsOrderedInPast = productProvider.getProductsOrderedInPast(allCartItems)

            val productCollectionsOrderedFromInPast = productCollectionProvider.getProductCollections(
                collectionIds = emptySet(),
                productIds = productsOrderedInPast.map { it.id }.toSet()
            )

            ShopViewForCustomerResponse(
                user = user?.toSavedUserResponse(),
                company = company.toSavedCompanyResponse(),
                allCollectionsWithProducts = allCollectionWithProducts.toAllCollectionsWithProductsResponse(),
                bestsellerProductsIds = bestsellerProducts.await().map { it.id }.toSet(),
                pastOrderedProductsIds = productsOrderedInPast.map { it.id }.toSet(),
                bestsellerCollectionsIds = bestsellerCollections.await().map { it.id }.toSet(),
                collectionsIdsOrderedFromInPast = productCollectionsOrderedFromInPast.mapNotNull { it.collection }.map { it.id }.toSet()
            )
        }
    }
}
