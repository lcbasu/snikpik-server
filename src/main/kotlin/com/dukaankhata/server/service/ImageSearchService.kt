package com.dukaankhata.server.service

import com.dukaankhata.server.dto.ThirdPartyImageSearchResponse

abstract class ImageSearchService {
    abstract fun getImagesForQuery(query: String): ThirdPartyImageSearchResponse?
}
