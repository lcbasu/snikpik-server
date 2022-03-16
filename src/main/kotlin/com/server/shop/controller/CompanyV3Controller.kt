package com.server.shop.controller

import com.server.shop.dto.SaveCompanyV3Request
import com.server.shop.dto.SavedCompanyV3Response
import com.server.shop.service.CompanyV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("shop/company/v3")
class CompanyV3Controller {

    @Autowired
    private lateinit var companyV3Service: CompanyV3Service

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveCompany(@RequestBody request: SaveCompanyV3Request): SavedCompanyV3Response? {
        return companyV3Service.saveCompany(request)
    }

}
