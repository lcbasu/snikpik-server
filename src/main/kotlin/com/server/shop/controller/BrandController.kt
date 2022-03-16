package com.server.shop.controller

import com.server.shop.dto.SaveBrandRequest
import com.server.shop.dto.SavedBrandResponse
import com.server.shop.service.BrandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("shop/brand")
class BrandController {

    @Autowired
    private lateinit var brandService: BrandService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveBrand(@RequestBody request: SaveBrandRequest): SavedBrandResponse? {
        return brandService.saveBrand(request)
    }

}
