package com.server.shop.service

import com.server.shop.dto.SaveBrandRequest
import com.server.shop.dto.SavedBrandResponse

abstract class BrandService {
    abstract fun saveBrand(request: SaveBrandRequest): SavedBrandResponse?

}
