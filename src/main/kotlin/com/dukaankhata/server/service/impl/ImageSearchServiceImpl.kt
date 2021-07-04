package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.ThirdPartyImageSearchResponse
import com.dukaankhata.server.service.ImageSearchService
import com.dukaankhata.server.provider.CacheProvider
import com.dukaankhata.server.provider.ImageSearchProvider
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ImageSearchServiceImpl : ImageSearchService() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var imageSearchProvider: ImageSearchProvider

    @Autowired
    private lateinit var cacheProvider: CacheProvider

    override fun getImagesForQuery(query: String): ThirdPartyImageSearchResponse? =
        runBlocking {
            val responseFromCache = cacheProvider.getThirdPartyImageSearchResponse(query).await()

            responseFromCache?.let {
                // Return the cached value
                logger.info("Retuning values for getImagesForQuery from cache")
                it
            } ?: imageSearchProvider.getImagesForQuery(query)
        }

}
