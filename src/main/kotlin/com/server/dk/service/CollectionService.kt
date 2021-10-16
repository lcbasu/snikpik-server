package com.server.dk.service

import com.server.dk.dto.AllCollectionsResponse
import com.server.dk.dto.AllCollectionsWithProductsResponse
import com.server.dk.dto.SaveCollectionRequest
import com.server.dk.dto.SavedCollectionResponse

abstract class CollectionService {
    abstract fun saveCollection(saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse?
    abstract fun getAllCollection(companyServerId: String): AllCollectionsResponse
    abstract fun getAllCollectionWithProducts(companyServerId: String): AllCollectionsWithProductsResponse
}
