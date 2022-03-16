package com.server.shop.service

import com.server.shop.dto.SaveCompanyV3Request
import com.server.shop.dto.SavedCompanyV3Response
import com.server.shop.dto.toSavedCompanyV3Response
import com.server.shop.provider.CompanyV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyV3ServiceImpl : CompanyV3Service() {

    @Autowired
    private lateinit var companyV3Provider: CompanyV3Provider

    override fun saveCompany(request: SaveCompanyV3Request): SavedCompanyV3Response? {
        return companyV3Provider.saveCompany(request).toSavedCompanyV3Response()
    }

}
