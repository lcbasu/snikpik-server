package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.ThirdPartyImageSearchResponse
import com.dukaankhata.server.service.ImageSearchService
import com.dukaankhata.server.utils.CacheUtils
import com.dukaankhata.server.utils.ImageSearchUtils
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
    private lateinit var imageSearchUtils: ImageSearchUtils

    @Autowired
    private lateinit var cacheUtils: CacheUtils

    override fun getImagesForQuery(query: String): ThirdPartyImageSearchResponse? =
        runBlocking {
            val responseFromCache = cacheUtils.getThirdPartyImageSearchResponse(query).await()

            responseFromCache?.let {
                // Return the cached value
                logger.info("Retuning values for getImagesForQuery from cache")
                it
            } ?: imageSearchUtils.getImagesForQuery(query)
        }

}
