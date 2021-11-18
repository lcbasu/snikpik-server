package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.entities.post.Post
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.service.faker.FakerService
import com.server.ud.service.post.PostService
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

}
