package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.AllCollectionsResponse
import com.dukaankhata.server.dto.AllCollectionsWithProductsResponse
import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse
import com.dukaankhata.server.service.CollectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("collection")
class CollectionController {
    @Autowired
    private lateinit var collectionService: CollectionService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveCollection(@RequestBody saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse? {
        return collectionService.saveCollection(saveCollectionRequest)
    }

    @RequestMapping(value = ["/getAllCollection/{companyServerId}"], method = [RequestMethod.GET])
    fun getAllCollection(@PathVariable companyServerId: String): AllCollectionsResponse {
        return collectionService.getAllCollection(companyServerId)
    }

    @RequestMapping(value = ["/getAllCollectionWithProducts/{companyServerId}"], method = [RequestMethod.GET])
    fun getAllCollectionWithProducts(@PathVariable companyServerId: String): AllCollectionsWithProductsResponse {
        return collectionService.getAllCollectionWithProducts(companyServerId)
    }
}
