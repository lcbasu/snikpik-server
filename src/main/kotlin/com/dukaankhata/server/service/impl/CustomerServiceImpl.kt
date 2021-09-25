package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.provider.*
import com.dukaankhata.server.service.CustomerService
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
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var discountProvider: DiscountProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var customerProvider: CustomerProvider

    override fun getShopViewForCustomer(shopUsername: String): ShopViewForCustomerResponse {
        // Only logged in user
        // Not signed in user also gets logged in as the anonymous user
        // So that no one else can make the api calls, unless logged in
        // Or on our websites
        val requestContext = authProvider.validateRequest()
        return customerProvider.getShopViewForCustomer(shopUsername, requestContext.user)
    }

    override fun getRelatedProducts(productId: String): RelatedProductsResponse? {
        val requestContext = authProvider.validateRequest()
        val relatedProducts = productProvider.getRelatedProducts(productId)
        return RelatedProductsResponse(
            products = relatedProducts.map { it.toSavedProductResponse() }
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
        return updatedCartData.updatedProductOrder.toSavedProductOrderResponse()
    }

    override fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val company = companyProvider.getCompanyByUsername(shopUsername) ?: error("Shop username is required")
        val activeProductOrderBag = productOrderProvider.getActiveProductOrderBag(
            company = company,
            user = user)
        return activeProductOrderBag?.toSavedProductOrderResponse()
    }

    override fun getActiveDiscounts(companyId: String): SavedActiveDiscountsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyId
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
            fromProductOrdersResponse.add(migratedCartData.fromProductOrder.toSavedProductOrderResponse())
            toProductOrdersResponse.add(migratedCartData.toProductOrder.toSavedProductOrderResponse())
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
        return productOrder.toSavedProductOrderResponse()
    }

    override fun getProductDetails(productId: String): SavedProductResponse {
        authProvider.validateRequest()
        val product = productProvider.getProduct(productId) ?: error("Product not found for id: $productId")
        return product.toSavedProductResponse()
    }

    override fun getProductOrders(): AllProductOrdersResponse {
        val requestContext = authProvider.validateRequest()
        val productOrders = productOrderProvider.getProductOrders(requestContext.user)
        return AllProductOrdersResponse(
            orders = productOrders.map { it.toSavedProductOrderResponse() }
        )
    }

}
