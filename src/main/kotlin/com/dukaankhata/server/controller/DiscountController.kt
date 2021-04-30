package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveDiscountRequest
import com.dukaankhata.server.dto.SavedDiscountResponse
import com.dukaankhata.server.service.DiscountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("discount")
class DiscountController {
    @Autowired
    private lateinit var discountService: DiscountService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveDiscountRequest: SaveDiscountRequest): SavedDiscountResponse {
        return discountService.saveDiscount(saveDiscountRequest)
    }
}
