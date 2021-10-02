package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

// For anything related to DKShop, we interact with
// this service
abstract class DKShopService {
    abstract fun saveOrUpdateExtraChargeDelivery(saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest): SavedExtraChargeDeliveryResponse
    abstract fun getExtraCharges(companyId: String): SavedExtraChargesResponse
    abstract fun saveOrUpdateExtraChargeTax(saveExtraChargeTaxRequest: SaveExtraChargeTaxRequest): SavedExtraChargeTaxResponse
    abstract fun getShopCompleteData(companyId: String): ShopCompleteDataResponse
}
