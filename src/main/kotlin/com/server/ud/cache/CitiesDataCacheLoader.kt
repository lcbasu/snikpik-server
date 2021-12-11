package com.server.dk.cache

import com.github.benmanes.caffeine.cache.CacheLoader
import com.server.common.provider.CSVDataProvider
import com.server.ud.dto.CityLocationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class CitiesDataCacheLoader(private val csvDataProvider: CSVDataProvider): CacheLoader<String, Map<String, CityLocationData>?> {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun reload(key: String, oldValue: Map<String, CityLocationData>): Map<String, CityLocationData>? = loadAsync(key).get()

    override fun asyncReload(key: String, oldValue: Map<String, CityLocationData>, executor: Executor): CompletableFuture<Map<String, CityLocationData>?> = loadAsync(key)

    override fun asyncLoad(key: String, executor: Executor): CompletableFuture<Map<String, CityLocationData>?> = loadAsync(key)

    override fun load(key: String): Map<String, CityLocationData>? = loadAsync(key).get()

    private fun loadAsync(key: String): CompletableFuture<Map<String, CityLocationData>?> {
        logger.info("run loadAsync for key: $key. Means there was a cache miss")
        return CoroutineScope(Dispatchers.Default).future {
            csvDataProvider.loadCitiesLocationData()
        }
    }
}
