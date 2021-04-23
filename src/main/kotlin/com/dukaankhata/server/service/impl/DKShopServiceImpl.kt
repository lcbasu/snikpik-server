package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.enums.TakeShopOnlineAfter
import com.dukaankhata.server.service.DKShopService
import com.dukaankhata.server.service.schedule.TakeShopOnlineSchedulerService
import com.dukaankhata.server.utils.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DKShopServiceImpl : DKShopService() {
    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var addressUtils: AddressUtils

    @Autowired
    private lateinit var productUtils: ProductUtils

    @Autowired
    private lateinit var cartItemUtils: CartItemUtils

    @Autowired
    private lateinit var collectionUtils: CollectionUtils

    @Autowired
    private lateinit var takeShopOnlineSchedulerService: TakeShopOnlineSchedulerService

    @Autowired
    private lateinit var productOrderUtils: ProductOrderUtils


    override fun saveUsername(saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveUsernameRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        if (company.username != null && company.username!!.isNotBlank()) {
            error("You can not edit username once it is added")
        }

        val isAvailable = companyUtils.isUsernameAvailable(saveUsernameRequest.username)

        if (isAvailable) {
            val updatedCompany = companyUtils.saveUsername(company, saveUsernameRequest.username) ?: error("Saving username failed")
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
        authUtils.validateRequest()

        return UsernameAvailableResponse(companyUtils.isUsernameAvailable(username))
    }

    override fun takeShopOffline(takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = takeShopOfflineRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val updatedCompany = companyUtils.takeShopOffline(company) ?: error("Company update failed")

        if (takeShopOfflineRequest.takeShopOnlineAfter != TakeShopOnlineAfter.MANUALLY) {
            takeShopOnlineSchedulerService.takeShopOnline(company, takeShopOfflineRequest.takeShopOnlineAfter)
        }

        return TakeShopOfflineResponse(
            takeShopOnlineAfter = takeShopOfflineRequest.takeShopOnlineAfter,
            company = updatedCompany.toSavedCompanyResponse()
        )
    }

    override fun saveAddress(saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveCompanyAddressRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company is required")

        val companyAddress = addressUtils.saveCompanyAddress(company, saveCompanyAddressRequest.name, saveCompanyAddressRequest.address) ?: error("Error while saveing company address")
        val newAddress = companyAddress.address ?: error("Address should always be present for companyAddress")
        val updatedCompany = companyUtils.updateCompanyDefaultAddress(company, newAddress) ?: error("Error while updating default address for comany")

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
            val requestContext = authUtils.validateRequest()

            val user = requestContext.user

            val company = companyUtils.getCompany(username) ?: error("Username not found")

            val allProductsFuture = async { productUtils.getProducts(company) }
            val allCollectionsFuture = async { collectionUtils.getCollections(company) }

            val allProducts = allProductsFuture.await()
            val allCollections = allCollectionsFuture.await()

            val allCartItemsFuture = async { cartItemUtils.getCartItemsForUserForProducts(user.id, allProducts.map { it.id }.toSet()) }

            val allCartItems = allCartItemsFuture.await()

            val bestsellerProducts = async { productUtils.getBestSellerProducts(allProducts) }

            val productsOrderedInPast = productUtils.getProductsOrderedInPast(allCartItems)

            val bestsellerCollections = async { collectionUtils.getBestSellerCollections(allCollections) }

            val productCollectionsOrderedFromInPast = productUtils.getProductCollections(
                collectionIds = emptySet(),
                productIds = productsOrderedInPast.map { it.id }.toSet()
            )

            ShopViewForCustomerResponse(
                user = user.toSavedUserResponse(),
                company = company.toSavedCompanyResponse(),
                allProducts = allProducts.map { it.toSavedProductResponse() },
                bestsellerProductsIds = bestsellerProducts.await().map { it.id }.toSet(),
                pastOrderedProductsIds = productsOrderedInPast.map { it.id }.toSet(),
                allCollections = allCollections.map { it.toSavedCollectionResponse() },
                bestsellerCollectionsIds = bestsellerCollections.await().map { it.id }.toSet(),
                collectionsIdsOrderedFromInPast = productCollectionsOrderedFromInPast.mapNotNull { it.collection }.map { it.id }.toSet()
            )
        }
    }

    override fun getRelatedProducts(productId: String): RelatedProductsResponse? {
        val requestContext = authUtils.validateRequest()
        val relatedProducts = productUtils.getRelatedProducts(productId)
        return RelatedProductsResponse(
            products = relatedProducts.map { it.toSavedProductResponse() }
        )
    }

    override fun updateCartRequest(updateCartRequest: UpdateCartRequest): SavedProductOrderResponse? {
        val requestContext = authUtils.validateRequest()

        val user = requestContext.user
        val product = productUtils.getProduct(updateCartRequest.productId) ?: error("Product is required")
        val company = product.company ?: error("Every product should always belong to a company")
        val activeProductOrderBag = productOrderUtils.getOrCreateActiveProductOrderBag(
            company = company,
            user = user)
        val updatedCartData = cartItemUtils.updateCartAndDependentOrder(
            company = company,
            user = user,
            product = product,
            productOrder = activeProductOrderBag,
            cartItemUpdateAction = updateCartRequest.action)
        return updatedCartData.updatedProductOrder.toSavedProductOrderResponse(updatedCartData.productOrderCartItems)
    }

    override fun getActiveProductOrderBag(shopUsername: String): SavedProductOrderResponse? {
        val requestContext = authUtils.validateRequest()
        val user = requestContext.user
        val company = companyUtils.getCompany(shopUsername) ?: error("Shop username is required")
        val activeProductOrderBag = productOrderUtils.getActiveProductOrderBag(
            company = company,
            user = user)
        val cartItems = activeProductOrderBag?.let { cartItemUtils.getCartItems(it) } ?: emptyList()
        return activeProductOrderBag?.toSavedProductOrderResponse(cartItems)
    }
}
