package com.dukaankhata.server.service

import com.dukaankhata.server.dto.AllCollectionsResponse
import com.dukaankhata.server.dto.AllCollectionsWithProductsResponse
import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse

abstract class CollectionService {
    abstract fun saveCollection(saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse?
    abstract fun getAllCollection(companyServerId: String): AllCollectionsResponse
    abstract fun getAllCollectionWithProducts(companyServerId: String): AllCollectionsWithProductsResponse
}
