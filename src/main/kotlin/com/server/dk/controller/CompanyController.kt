package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.CompanyService
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

    @RequestMapping(value = ["/getCompany/{companyServerIdOrUsername}"], method = [RequestMethod.GET])
    fun getCompany(@PathVariable companyServerIdOrUsername: String): SavedCompanyResponse? {
        return companyService.getCompany(companyServerIdOrUsername)
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
    fun updateUsername(@RequestBody updateCompanyUsernameRequest: UpdateCompanyUsernameRequest): SavedCompanyResponse? {
        return companyService.updateUsername(updateCompanyUsernameRequest)
    }

    @RequestMapping(value = ["/updateName"], method = [RequestMethod.POST])
    fun updateName(@RequestBody updateCompanyNameRequest: UpdateCompanyNameRequest): SavedCompanyResponse? {
        return companyService.updateName(updateCompanyNameRequest)
    }

    @RequestMapping(value = ["/updateMobile"], method = [RequestMethod.POST])
    fun updateMobile(@RequestBody updateCompanyMobileRequest: UpdateCompanyMobileRequest): SavedCompanyResponse? {
        return companyService.updateMobile(updateCompanyMobileRequest)
    }

    @RequestMapping(value = ["/updateCategoryGroup"], method = [RequestMethod.POST])
    fun updateCategoryGroup(@RequestBody request: UpdateCompanyCategoryGroupRequest): SavedCompanyResponse? {
        return companyService.updateCategoryGroup(request)
    }

    @RequestMapping(value = ["/updateLogo"], method = [RequestMethod.POST])
    fun updateLogo(@RequestBody updateCompanyLogoRequest: UpdateCompanyLogoRequest): SavedCompanyResponse? {
        return companyService.updateLogo(updateCompanyLogoRequest)
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
    fun saveAddress(@RequestBody saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyResponse? {
        return companyService.saveAddress(saveCompanyAddressRequest)
    }

    @RequestMapping(value = ["/getAddresses/{companyServerIdOrUsername}"], method = [RequestMethod.GET])
    fun getAddresses(@PathVariable companyServerIdOrUsername: String): CompanyAddressesResponse {
        return companyService.getAddresses(companyServerIdOrUsername)
    }
}
