package com.server.dk.service.impl

import com.server.dk.dto.*
import com.server.dk.provider.*
import com.server.dk.service.DKShopService
import com.server.dk.utils.CommonUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DKShopServiceImpl : DKShopService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    @Autowired
    private lateinit var extraChargeDeliveryProvider: ExtraChargeDeliveryProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var extraChargeTaxProvider: ExtraChargeTaxProvider

    override fun saveOrUpdateExtraChargeDelivery(saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest): SavedExtraChargeDeliveryResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = saveExtraChargeDeliveryRequest.companyId
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
            companyServerIdOrUsername = saveExtraChargeTaxRequest.companyId
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
                companyServerIdOrUsername = companyId
            )
            val company = requestContext.company ?: error("Company is required")

            val productsFuture = async { productProvider.getProducts(company) }

            val collections = collectionProvider.getCollections(company)

            val productCollections = productCollectionProvider.getProductCollections(collectionIds = collections.map { it.id }.toSet()).mapNotNull { pc ->
                async {
                    if (pc.collection != null && pc.product != null) {
                        ProductCollectionResponse(
                            serverId = pc.collection!!.id + CommonUtils.STRING_SEPARATOR + pc.product!!.id,
                            company = company.toSavedCompanyResponse(),
                            collection = pc.collection!!.toSavedCollectionResponse(),
                            product = pc.product!!.toSavedProductResponse(),
                        )
                    } else {
                        null
                    }
                }
            }.mapNotNull { it.await() }

            ShopCompleteDataResponse(
                company = company.toSavedCompanyResponse(),
                products = productsFuture.await().map { it.toSavedProductResponse() },
                collections = collections.map { it.toSavedCollectionResponse() },
                productCollections = productCollections
            )
        }
    }

    override fun getExtraCharges(companyId: String): SavedExtraChargesResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyId
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
