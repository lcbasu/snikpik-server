package com.server.shop.service

import com.server.shop.dto.SaveCompanyV3Request
import com.server.shop.dto.SavedCompanyV3Response

abstract class CompanyV3Service {
    abstract fun saveCompany(request: SaveCompanyV3Request): SavedCompanyV3Response?

}
