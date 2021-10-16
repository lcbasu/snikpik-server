package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.DKShopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("dkShop")
class DKShopController {

    @Autowired
    private lateinit var dkShopService: DKShopService

    @RequestMapping(value = ["/saveOrUpdateExtraChargeDelivery"], method = [RequestMethod.POST])
    fun saveOrUpdateExtraChargeDelivery(@RequestBody saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest): SavedExtraChargeDeliveryResponse {
        return dkShopService.saveOrUpdateExtraChargeDelivery(saveExtraChargeDeliveryRequest)
    }

    @RequestMapping(value = ["/saveOrUpdateExtraChargeTax"], method = [RequestMethod.POST])
    fun saveOrUpdateExtraChargeTax(@RequestBody saveExtraChargeTaxRequest: SaveExtraChargeTaxRequest): SavedExtraChargeTaxResponse {
        return dkShopService.saveOrUpdateExtraChargeTax(saveExtraChargeTaxRequest)
    }

    @RequestMapping(value = ["/getExtraCharges/{companyId}"], method = [RequestMethod.GET])
    fun getExtraCharges(@PathVariable companyId: String): SavedExtraChargesResponse {
        return dkShopService.getExtraCharges(companyId)
    }

    @RequestMapping(value = ["/getShopCompleteData/{companyId}"], method = [RequestMethod.GET])
    fun getShopCompleteData(@PathVariable companyId: String): ShopCompleteDataResponse {
        return dkShopService.getShopCompleteData(companyId)
    }
}
