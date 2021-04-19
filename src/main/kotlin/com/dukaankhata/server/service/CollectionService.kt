package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse

abstract class CollectionService {
    abstract fun saveCollection(saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse?
}
