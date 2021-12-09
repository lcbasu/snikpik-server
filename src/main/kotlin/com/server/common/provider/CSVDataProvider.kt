package com.server.common.provider

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

data class CityLocationData(
    val city: String,
    val state: String,
    val country: String,
    val countryCode: String,
    val zipcode: String,
    val latitude: Double,
    val longitude: Double,
)

object CSVDataProvider {
    val schema = CsvSchema.emptySchema().withHeader()
    val mapper = CsvMapper().apply {
        registerKotlinModule()
        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }

    val citiesLocationData: Map<String, CityLocationData>

    init {
        citiesLocationData = loadCitiesLocationData()
    }

    private fun loadCitiesLocationData(): Map<String, CityLocationData> {
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
        return result.toSortedMap()
    }
}
