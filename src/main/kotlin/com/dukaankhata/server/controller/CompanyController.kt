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

    @RequestMapping(value = ["/getUserCompanies/{phoneNumber}"], method = [RequestMethod.GET])
    fun getUserCompanies(@PathVariable phoneNumber: String): UserCompaniesResponse? {
        return companyService.getUserCompanies(phoneNumber)
    }

    @RequestMapping(value = ["/isUsernameAvailable/{username}"], method = [RequestMethod.GET])
    fun isUsernameAvailable(@PathVariable username: String): UsernameAvailableResponse? {
        return companyService.isUsernameAvailable(username)
    }

    @RequestMapping(value = ["/saveUsername"], method = [RequestMethod.POST])
    fun saveUsername(@RequestBody saveUsernameRequest: SaveUsernameRequest): SaveUsernameResponse? {
        return companyService.saveUsername(saveUsernameRequest)
    }

    @RequestMapping(value = ["/takeShopOffline"], method = [RequestMethod.POST])
    fun takeShopOffline(@RequestBody takeShopOfflineRequest: TakeShopOfflineRequest): TakeShopOfflineResponse? {
        return companyService.takeShopOffline(takeShopOfflineRequest)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveCompanyAddressRequest: SaveCompanyAddressRequest): SavedCompanyAddressResponse? {
        return companyService.saveAddress(saveCompanyAddressRequest)
    }
}
