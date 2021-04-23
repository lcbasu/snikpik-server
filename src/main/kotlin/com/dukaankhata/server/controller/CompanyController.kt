package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveCompanyRequest
import com.dukaankhata.server.dto.SavedCompanyResponse
import com.dukaankhata.server.dto.UserCompaniesResponse
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
}
