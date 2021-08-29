package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.CategoryGroupsResponse
import com.dukaankhata.server.service.CatalogueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("catalogue")
class CatalogueController {

    @Autowired
    private lateinit var catalogueService: CatalogueService

    @RequestMapping(value = ["/getCategoryGroups"], method = [RequestMethod.GET])
    fun getCategoryGroups(): CategoryGroupsResponse {
        return catalogueService.getCategoryGroups()
    }
}
