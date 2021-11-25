package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.faker.FakerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/faker")
class FakerController {

    @Autowired
    private lateinit var fakerService: FakerService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun createFakeData(@RequestBody request: FakerRequest): FakerResponse {
        return fakerService.createFakeData(request)
    }

    @RequestMapping(value = ["/createFakeDataRandomly"], method = [RequestMethod.POST])
    fun createFakeDataRandomly(): String {
        return fakerService.createFakeDataRandomly()
    }

    @RequestMapping(value = ["/doSomething"], method = [RequestMethod.GET])
    fun doSomething(): Any {
        return fakerService.doSomething()
    }

}
