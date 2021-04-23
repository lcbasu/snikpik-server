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

    @RequestMapping(value = ["/saveUsername"], method = [RequestMethod.POST])
    fun saveUsername(@RequestBody saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
        return dkShopService.saveUsername(saveUsernameRequest)
    }

    @RequestMapping(value = ["/takeShopOffline"], method = [RequestMethod.POST])
    fun takeShopOffline(@RequestBody takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse? {
        return dkShopService.takeShopOffline(takeShopOfflineRequest)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        return dkShopService.saveAddress(saveCompanyAddressRequest)
    }
}
