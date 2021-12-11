package com.server.common.provider

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.server.ud.dto.CityLocationData
import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var s3Client: AmazonS3

    fun loadCitiesLocationData(): Map<String, CityLocationData> {
        return try {
            val result = mutableMapOf<String, CityLocationData>()

            val localFile = File("cities_location.csv")
            s3Client.getObject(GetObjectRequest("unbox-server-data-prod", "cities_location.csv"), localFile)
            val reader = mapper.readerFor(CityLocationData::class.java)
            reader
                .with(schema)
                .readValues<CityLocationData>(localFile)
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
            localFile.delete()
            result.toSortedMap()
        } catch (e: Exception) {
            logger.error("Error while reading CSV data for cities.")
            e.printStackTrace()
            Sentry.captureException(e)
            emptyMap()
        }
    }
}
