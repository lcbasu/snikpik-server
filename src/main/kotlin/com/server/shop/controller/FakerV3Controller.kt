package com.server.shop.controller

import com.server.shop.service.FakerV3Service
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("shop/faker/v3")
class FakerV3Controller {

    @Autowired
    private lateinit var fakerV3Service: FakerV3Service

    @RequestMapping(value = ["/generateFakeShopData"], method = [RequestMethod.POST])
    fun generateFakeShopData(): Any {
        return fakerV3Service.generateFakeShopData()
    }

    @RequestMapping(value = ["/doSomething"], method = [RequestMethod.GET])
    fun doSomething(): Any {
        return fakerV3Service.doSomething()
    }

}
