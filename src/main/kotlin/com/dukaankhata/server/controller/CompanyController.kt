package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.company.SaveCompanyRequest
import com.dukaankhata.server.dto.company.SavedCompanyResponse
import com.dukaankhata.server.service.CompanyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("company")
class CompanyController {
    @Autowired
    var companyService: CompanyService? = null

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveCompanyRequest: SaveCompanyRequest): SavedCompanyResponse? {
        return companyService?.saveCompany(saveCompanyRequest)
    }
}
