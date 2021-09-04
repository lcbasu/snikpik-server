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
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var takeShopOnlineSchedulerService: TakeShopOnlineSchedulerService

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
