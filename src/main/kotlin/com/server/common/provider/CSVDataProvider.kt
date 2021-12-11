package com.server.common.provider

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.server.ud.dto.CityLocationData
import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

@Component
class CSVDataProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    val schema = CsvSchema.emptySchema().withHeader()
    val mapper = CsvMapper().apply {
        registerKotlinModule()
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }

//    final val citiesLocationData: Map<String, CityLocationData>

//    init {
//        citiesLocationData = loadCitiesLocationData()
//    }

    fun loadCitiesLocationData(): Map<String, CityLocationData> {
        return try {
            val result = mutableMapOf<String, CityLocationData>()
            val reader = mapper.readerFor(CityLocationData::class.java)
            reader
                .with(schema)
                .readValues<CityLocationData>(
                    File::class.java.getResourceAsStream("/cities_location.csv")
                )
                .readAll()
                .map {
                    if (result.containsKey(it.city)) {
                        // Same city is appearing twice
                        // So check if the state is different
                        // and then store it with state name
                        val oldValue = result.getValue(it.city)
                        if (oldValue.state != it.state) {
                            val cityName = "${it.city} - (${it.state})"
                            result[cityName] = it.copy(city = cityName)
                        }
                    } else {
                        result[it.city] = it
                    }
                }
            result.toSortedMap()
        } catch (e: Exception) {
            logger.error("Error while reading CSV data for cities.")
            e.printStackTrace()
            Sentry.captureException(e)
            emptyMap()
        }
    }
}
