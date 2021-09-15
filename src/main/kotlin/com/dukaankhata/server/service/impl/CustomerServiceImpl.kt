package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.provider.*
import com.dukaankhata.server.service.CustomerService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl : CustomerService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var discountProvider: DiscountProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    override fun getShopViewForCustomer(username: String): ShopViewForCustomerResponse {
        return runBlocking {
            // Only logged in user
            // Not signed in user also gets logged in as the anonymous user
            // So that no one else can make the api calls, unless logged in
            // Or on our websites
            val requestContext = authProvider.validateRequest()

            val user = requestContext.user

            val company = companyProvider.getCompanyByUsername(username) ?: error("Username not found")

            val allProductVariantsFuture = async { productVariantProvider.getProductVariants(company) }
            val allCollectionsFuture = async { collectionProvider.getCollections(company) }

            val allProductVariants = allProductVariantsFuture.await()
            val allProducts = allProductVariants.mapNotNull { it.product }
            val allCollections = allCollectionsFuture.await()

            val allCartItemsFuture = async { cartItemProvider.getCartItemsForUserForProductVariants(user.id, allProductVariants.map { it.id }.toSet()) }

            val allCartItems = allCartItemsFuture.await()

            val bestsellerProducts = async { productProvider.getBestSellerProducts(allProducts) }

            val productsOrderedInPast = productProvider.getProductsOrderedInPast(allCartItems)

            val bestsellerCollections = async { collectionProvider.getBestSellerCollections(allCollections) }

            val productCollectionsOrderedFromInPast = productCollectionProvider.getProductCollections(
                collectionIds = emptySet(),
                productIds = productsOrderedInPast.map { it.id }.toSet()
            )

            ShopViewForCustomerResponse(
                user = user.toSavedUserResponse(),
                company = company.toSavedCompanyResponse(),
                allProducts = allProducts.map { it.toSavedProductResponse(productVariantProvider, productCollectionProvider) },
                bestsellerProductsIds = bestsellerProducts.await().map { it.id }.toSet(),
                pastOrderedProductsIds = productsOrderedInPast.map { it.id }.toSet(),
                allCollections = allCollections.map { it.toSavedCollectionResponse() },
                bestsellerCollectionsIds = bestsellerCollections.await().map { it.id }.toSet(),
                collectionsIdsOrderedFromInPast = productCollectionsOrderedFromInPast.mapNotNull { it.collection }.map { it.id }.toSet()
            )
        }
    }

    override fun getRelatedProducts(productId: String): RelatedProductsResponse? {
        val requestContext = authProvider.validateRequest()
        val relatedProducts = productProvider.getRelatedProducts(productId)
        return RelatedProductsResponse(
            products = relatedProducts.map { it.toSavedProductResponse(productVariantProvider, productCollectionProvider) }
        )
    }

    override fun updateCart(updateCartRequest: UpdateCartRequest): SavedProductOrderResponse? {
        val requestContext = authProvider.validateRequest()

        val user = requestContext.user
        val productVariant = productVariantProvider.getProductVariant(updateCartRequest.productVariantId) ?: error("Product Variant is required")
        val company = productVariant.company ?: error("Every product variant should always belong to a company")
        val activeProductOrderBag = productOrderProvider.getOrCreateActiveProductOrderBag(
            company = company,
            user = user)
        val updatedCartData = cartItemProvider.updateCartAndDependentOrder(
            company = company,
            user = user,
            productVariant = productVariant,
            productOrder = activeProductOrderBag,
            cartItemUpdateAction = updateCartRequest.action,
            newQuantity = updateCartRequest.newQuantity)
        return updatedCartData.updatedProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val company = companyProvider.getCompanyByUsername(shopUsername) ?: error("Shop username is required")
        val activeProductOrderBag = productOrderProvider.getActiveProductOrderBag(
            company = company,
            user = user)
        return activeProductOrderBag?.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun getActiveDiscounts(companyId: String): SavedActiveDiscountsResponse {
        val requestContext = authProvider.validateRequest(
            companyId = companyId
        )
        val company = requestContext.company ?: error("Company is required")
        val activeDiscounts = discountProvider.getActiveDiscounts(company)
        return SavedActiveDiscountsResponse(
            company = company.toSavedCompanyResponse(),
            discounts = activeDiscounts.map { it.toSavedDiscountResponse() }
        )
    }

    override fun migrateCart(migrateCartRequest: MigrateCartRequest): MigratedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val toUser = requestContext.user
        if (toUser.id != migrateCartRequest.toUserId) {
            error("Cart can only be migrated for users who logged in through phone number " +
                "and want to move their non-logged in cart to logged in cart")
        }
        val fromUser = authProvider.getUser(migrateCartRequest.fromUserId) ?: error("From user is required")

        val fromProductOrders = productOrderProvider.getActiveProductOrderBag(fromUser)

        if (fromProductOrders.isEmpty()) {
            error("Current user does not have any active bag for any company")
        }

        val fromProductOrdersResponse = mutableListOf<SavedProductOrderResponse>()
        val toProductOrdersResponse = mutableListOf<SavedProductOrderResponse>()

        fromProductOrders.map { fromProductOrder ->
            val company = fromProductOrder.company ?: error("Order: ${fromProductOrder.id} does not have company")
            val toProductOrder = productOrderProvider.getOrCreateActiveProductOrderBag(
                company = company,
                user = toUser)
            val migratedCartData = cartItemProvider.migrateCart(
                fromProductOrder = fromProductOrder,
                toProductOrder = toProductOrder)
            fromProductOrdersResponse.add(migratedCartData.fromProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider))
            toProductOrdersResponse.add(migratedCartData.toProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider))
        }
        return MigratedProductOrderResponse(
            fromUser = fromUser.toSavedUserResponse(),
            toUser = toUser.toSavedUserResponse(),
            fromProductOrders = fromProductOrdersResponse,
            toProductOrders = toProductOrdersResponse
        )
    }

    override fun getProductOrder(productOrderId: String): SavedProductOrderResponse {
        val requestContext = authProvider.validateRequest()
        val productOrder = productOrderProvider.getProductOrder(productOrderId) ?: error("Product order not found for id: $productOrderId")
        val orderedUser = productOrder.addedBy ?: error("Product order with id: $productOrderId, does not have user")
        if (requestContext.user.id != orderedUser.id) {
            error("Only the customer can get the details of their order")
        }
        return productOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider, productCollectionProvider)
    }

    override fun getProductDetails(productId: String): SavedProductResponse {
        authProvider.validateRequest()
        val product = productProvider.getProduct(productId) ?: error("Product not found for id: $productId")
        return product.toSavedProductResponse(productVariantProvider, productCollectionProvider)
    }

}
