package com.server.shop.service

import com.server.shop.dto.SaveBrandRequest
import com.server.shop.dto.SavedBrandResponse
import com.server.shop.dto.toSavedBrandResponse
import com.server.shop.provider.BrandProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BrandServiceImpl : BrandService() {

    @Autowired
    private lateinit var brandProvider: BrandProvider

    override fun saveBrand(request: SaveBrandRequest): SavedBrandResponse? {
        return brandProvider.saveBrand(request).toSavedBrandResponse()
    }

}
