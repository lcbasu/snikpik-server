package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.DKShopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("dkShop")
class DKShopController {

    @Autowired
    private lateinit var dkShopService: DKShopService

    @RequestMapping(value = ["/isUsernameAvailable/{username}"], method = [RequestMethod.GET])
    fun isUsernameAvailable(@PathVariable username: String): UsernameAvailableResponse? {
        return dkShopService.isUsernameAvailable(username)
    }

//    @RequestMapping(value = ["/saveUsername"], method = [RequestMethod.POST])
//    fun saveUsername(@RequestBody saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
//        return dkShopService.saveUsername(saveUsernameRequest)
//    }

    @RequestMapping(value = ["/takeShopOffline"], method = [RequestMethod.POST])
    fun takeShopOffline(@RequestBody takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse? {
        return dkShopService.takeShopOffline(takeShopOfflineRequest)
    }

    @RequestMapping(value = ["/takeShopOnlineNow"], method = [RequestMethod.POST])
    fun takeShopOnlineNow(@RequestBody takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse? {
        return dkShopService.takeShopOnlineNow(takeShopOnlineNowRequest)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        return dkShopService.saveAddress(saveCompanyAddressRequest)
    }

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

    @RequestMapping(value = ["/getAddresses/{companyServerIdOrUsername}"], method = [RequestMethod.GET])
    fun getAddresses(@PathVariable companyServerIdOrUsername: String): CompanyAddressesResponse {
        return dkShopService.getAddresses(companyServerIdOrUsername)
    }
}
