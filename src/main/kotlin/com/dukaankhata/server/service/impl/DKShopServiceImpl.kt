package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.dukaankhata.server.provider.*
import com.dukaankhata.server.service.DKShopService
import com.dukaankhata.server.service.schedule.TakeShopOnlineSchedulerService
import com.dukaankhata.server.utils.CommonUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DKShopServiceImpl : DKShopService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var addressProvider: AddressProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var takeShopOnlineSchedulerService: TakeShopOnlineSchedulerService

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var discountProvider: DiscountProvider

    @Autowired
    private lateinit var extraChargeDeliveryProvider: ExtraChargeDeliveryProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var extraChargeTaxProvider: ExtraChargeTaxProvider

    override fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = saveUsernameRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        if (company.username != null && company.username!!.isNotBlank()) {
            error("You can not edit username once it is added")
        }

        val isAvailable = companyProvider.isUsernameAvailable(saveUsernameRequest.username)

        if (isAvailable) {
            val updatedCompany = companyProvider.saveUsername(requestContext.user, company, saveUsernameRequest.username) ?: error("Saving username failed")
            return SaveUsernameResponse(
                available = true,
                company = updatedCompany.toSavedCompanyResponse()
            )
        }
        return SaveUsernameResponse(
            available = false,
            company = null
        )
    }

    override fun isUsernameAvailable(username: String): UsernameAvailableResponse? {
        // To verify if the user is logged in
        authProvider.validateRequest()

        return UsernameAvailableResponse(companyProvider.isUsernameAvailable(username))
    }

    override fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = takeShopOfflineRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val updatedCompany = companyProvider.takeShopOffline(company) ?: error("Company update failed")

        if (takeShopOfflineRequest.takeShopOnlineAfter != TakeShopOnlineAfter.MANUALLY) {
            takeShopOnlineSchedulerService.takeShopOnline(company, takeShopOfflineRequest.takeShopOnlineAfter)
        }

        return updatedCompany.toSavedCompanyResponse()
    }

    override fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = saveCompanyAddressRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val companyAddress = addressProvider.saveCompanyAddress(company, saveCompanyAddressRequest.name, saveCompanyAddressRequest.address) ?: error("Error while saveing company address")
        val newAddress = companyAddress.address ?: error("Address should always be present for companyAddress")
        val updatedCompany = companyProvider.updateCompanyDefaultAddress(company, newAddress) ?: error("Error while updating default address for comany")

        return SavedCompanyAddressResponse(
            company = updatedCompany.toSavedCompanyResponse(),
            address = newAddress.toSavedAddressResponse()
        )
    }

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

            val productCollectionsOrderedFromInPast = productProvider.getProductCollections(
                collectionIds = emptySet(),
                productIds = productsOrderedInPast.map { it.id }.toSet()
            )

            ShopViewForCustomerResponse(
                user = user.toSavedUserResponse(),
                company = company.toSavedCompanyResponse(),
                allProducts = allProducts.map { it.toSavedProductResponse(productVariantProvider = productVariantProvider) },
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
            products = relatedProducts.map { it.toSavedProductResponse(productVariantProvider) }
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
            cartItemUpdateAction = updateCartRequest.action)
        return updatedCartData.updatedProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider)
    }

    override fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val company = companyProvider.getCompanyByUsername(shopUsername) ?: error("Shop username is required")
        val activeProductOrderBag = productOrderProvider.getActiveProductOrderBag(
            company = company,
            user = user)
        return activeProductOrderBag?.toSavedProductOrderResponse(productVariantProvider, cartItemProvider)
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

    override fun saveOrUpdateExtraChargeDelivery(saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest): SavedExtraChargeDeliveryResponse {
        val requestContext = authProvider.validateRequest(
            companyId = saveExtraChargeDeliveryRequest.companyId
        )
        val company = requestContext.company ?: error("Company is required")
        val ed = extraChargeDeliveryProvider.saveOrUpdateExtraChargeDelivery(
            addedBy = requestContext.user,
            company = company,
            saveExtraChargeDeliveryRequest = saveExtraChargeDeliveryRequest
        )
        return ed.toSavedExtraChargeDeliveryResponse()
    }

    override fun saveOrUpdateExtraChargeTax(saveExtraChargeTaxRequest: SaveExtraChargeTaxRequest): SavedExtraChargeTaxResponse {
        val requestContext = authProvider.validateRequest(
            companyId = saveExtraChargeTaxRequest.companyId
        )
        val company = requestContext.company ?: error("Company is required")
        val et = extraChargeTaxProvider.saveOrUpdateExtraChargeTax(
            addedBy = requestContext.user,
            company = company,
            saveExtraChargeTaxRequest = saveExtraChargeTaxRequest
        )
        return et.toSavedExtraChargeTaxResponse()
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
            fromProductOrdersResponse.add(migratedCartData.fromProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider))
            toProductOrdersResponse.add(migratedCartData.toProductOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider))
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
        return productOrder.toSavedProductOrderResponse(productVariantProvider, cartItemProvider)
    }

    override fun getShopCompleteData(companyId: String): ShopCompleteDataResponse {
        return runBlocking {
            val requestContext = authProvider.validateRequest(
                companyId = companyId
            )
            val company = requestContext.company ?: error("Company is required")

            val productsFuture = async { productProvider.getProducts(company) }

            val collections = collectionProvider.getCollections(company)

            val productCollections = productProvider.getProductCollections(collectionIds = collections.map { it.id }.toSet()).mapNotNull { pc ->
                async {
                    if (pc.collection != null && pc.product != null) {
                        ProductCollectionResponse(
                            serverId = pc.collection!!.id + CommonUtils.STRING_SEPARATOR + pc.product!!.id,
                            company = company.toSavedCompanyResponse(),
                            collection = pc.collection!!.toSavedCollectionResponse(),
                            product = pc.product!!.toSavedProductResponse(productVariantProvider),
                        )
                    } else {
                        null
                    }
                }
            }.mapNotNull { it.await() }

            ShopCompleteDataResponse(
                company = company.toSavedCompanyResponse(),
                products = productsFuture.await().map { it.toSavedProductResponse(productVariantProvider) },
                collections = collections.map { it.toSavedCollectionResponse() },
                productCollections = productCollections
            )
        }
    }

    override fun takeShopOnlineNow(takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = takeShopOnlineNowRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")
        val updatedCompany = companyProvider.takeShopOnline(company) ?: error("Company update failed")
        return updatedCompany.toSavedCompanyResponse()
    }

    override fun getExtraCharges(companyId: String): SavedExtraChargesResponse {
        val requestContext = authProvider.validateRequest(
            companyId = companyId
        )
        val company = requestContext.company ?: error("Company is required")
        val eds = extraChargeDeliveryProvider.getExtraChargeDeliveries(company)
        val ets = extraChargeTaxProvider.getExtraChargeTaxes(company)

        return SavedExtraChargesResponse(
            company = company.toSavedCompanyResponse(),
            charges =
            eds.map { it.toSavedExtraChargeDeliveryResponse() } +
                ets.map { it.toSavedExtraChargeTaxResponse() }
        )
    }
}
