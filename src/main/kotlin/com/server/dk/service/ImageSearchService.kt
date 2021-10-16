package com.server.dk.service

import com.server.dk.dto.ThirdPartyImageSearchResponse

abstract class ImageSearchService {
    abstract fun getImagesForQuery(query: String): ThirdPartyImageSearchResponse?
}
