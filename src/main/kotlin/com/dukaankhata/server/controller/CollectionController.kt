package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse
import com.dukaankhata.server.service.CollectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("collection")
class CollectionController {
    @Autowired
    private lateinit var collectionService: CollectionService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveCollection(@RequestBody saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse? {
        return collectionService.saveCollection(saveCollectionRequest)
    }
}
