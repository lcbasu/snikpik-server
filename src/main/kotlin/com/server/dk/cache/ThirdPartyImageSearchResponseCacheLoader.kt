package com.server.dk.cache

import com.server.dk.dto.ThirdPartyImageSearchResponse
import com.server.dk.provider.ImageSearchProvider
import com.github.benmanes.caffeine.cache.CacheLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class ThirdPartyImageSearchResponseCacheLoader(private val imageSearchProvider: ImageSearchProvider): CacheLoader<String, ThirdPartyImageSearchResponse?> {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    override fun reload(key: String, oldValue: ThirdPartyImageSearchResponse): ThirdPartyImageSearchResponse? = loadAsync(key).get()

    override fun asyncReload(key: String, oldValue: ThirdPartyImageSearchResponse, executor: Executor): CompletableFuture<ThirdPartyImageSearchResponse?> = loadAsync(key)

    override fun asyncLoad(key: String, executor: Executor): CompletableFuture<ThirdPartyImageSearchResponse?> = loadAsync(key)

    override fun load(key: String): ThirdPartyImageSearchResponse? = loadAsync(key).get()

    private fun loadAsync(key: String): CompletableFuture<ThirdPartyImageSearchResponse?> {
        val query = KeyBuilder.parseKeyForThirdPartyImageSearchResponseCache(key)
        logger.info("run loadAsync for key: $key")
        logger.info("run loadAsync for query: $query")
        return CoroutineScope(Dispatchers.Default).future {
            imageSearchProvider.getImagesForQuery(query)
        }
    }
}
