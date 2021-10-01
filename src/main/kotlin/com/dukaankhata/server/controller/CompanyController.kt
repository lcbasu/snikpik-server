package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.CompanyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("company")
class CompanyController {
    @Autowired
    private lateinit var companyService: CompanyService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveCompany(@RequestBody saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        return companyService.saveCompany(saveCompanyRequest)
    }

    @RequestMapping(value = ["/getUserCompanies/{absoluteMobile}"], method = [RequestMethod.GET])
    fun getUserCompanies(@PathVariable absoluteMobile: String): UserCompaniesResponse? {
        return companyService.getUserCompanies(absoluteMobile)
    }

    @RequestMapping(value = ["/isUsernameAvailable/{username}"], method = [RequestMethod.GET])
    fun isUsernameAvailable(@PathVariable username: String): UsernameAvailableResponse? {
        return companyService.isUsernameAvailable(username)
    }
//
//    @RequestMapping(value = ["/saveUsername"], method = [RequestMethod.POST])
//    fun saveUsername(@RequestBody saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
//        return companyService.saveUsername(saveUsernameRequest)
//    }

    @RequestMapping(value = ["/updateUsername"], method = [RequestMethod.POST])
    fun updateUsername(@RequestBody updateUsernameRequest: UpdateUsernameRequest): SavedCompanyResponse? {
        return companyService.updateUsername(updateUsernameRequest)
    }

    @RequestMapping(value = ["/updateName"], method = [RequestMethod.POST])
    fun updateName(@RequestBody updateNameRequest: UpdateNameRequest): SavedCompanyResponse? {
        return companyService.updateName(updateNameRequest)
    }

    @RequestMapping(value = ["/updateMobile"], method = [RequestMethod.POST])
    fun updateMobile(@RequestBody updateMobileRequest: UpdateMobileRequest): SavedCompanyResponse? {
        return companyService.updateMobile(updateMobileRequest)
    }

    @RequestMapping(value = ["/updateLogo"], method = [RequestMethod.POST])
    fun updateLogo(@RequestBody updateLogoRequest: UpdateLogoRequest): SavedCompanyResponse? {
        return companyService.updateLogo(updateLogoRequest)
    }

    @RequestMapping(value = ["/takeShopOffline"], method = [RequestMethod.POST])
    fun takeShopOffline(@RequestBody takeShopOfflineRequest: TakeShopOfflineRequest): SavedCompanyResponse? {
        return companyService.takeShopOffline(takeShopOfflineRequest)
    }

    @RequestMapping(value = ["/takeShopOnlineNow"], method = [RequestMethod.POST])
    fun takeShopOnlineNow(@RequestBody takeShopOnlineNowRequest: TakeShopOnlineNowRequest): SavedCompanyResponse? {
        return companyService.takeShopOnlineNow(takeShopOnlineNowRequest)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        return companyService.saveAddress(saveCompanyAddressRequest)
    }

    @RequestMapping(value = ["/getAddresses/{companyServerIdOrUsername}"], method = [RequestMethod.GET])
    fun getAddresses(@PathVariable companyServerIdOrUsername: String): CompanyAddressesResponse {
        return companyService.getAddresses(companyServerIdOrUsername)
    }
}
