package com.server.ud.provider.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.server.common.provider.CSVDataProvider
import com.server.dk.cache.CitiesDataCacheLoader
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class UDCacheProvider(private var csvDataProvider: CSVDataProvider) {

    private val citiesDataCacheLoader =
        CitiesDataCacheLoader(csvDataProvider)

    private val citiesDataCache by lazy {
        Caffeine
            .newBuilder()
            .maximumSize(1) // Store only one key
            .expireAfterWrite(1, TimeUnit.DAYS)
            .buildAsync(citiesDataCacheLoader)
    }

    fun getCitiesData() =
        // No actual key is required so just simulating one
        citiesDataCache.get("CITIES_LOCATION_DATA")

}
