package com.server.common.provider

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.server.ud.dto.CityLocationData
import io.sentry.Sentry
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File


@Component
class CSVDataProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

//    final val citiesLocationData: Map<String, CityLocationData>

//    init {
//        citiesLocationData = loadCitiesLocationData()
//    }

    @Autowired
    private lateinit var s3Client: AmazonS3

    fun getRowsFromCSVFile(fileName: String): List<CSVRecord> {
        return try {

            val localFile = File("localFilename.csv")

            s3Client.getObject(GetObjectRequest("unbox-server-data-prod", "cities_location.csv"), localFile)

//            localFile.reader()

//            val file = File(this.javaClass.classLoader.getResource(fileName).file)
            val reader = localFile.reader()//file.reader()
            val rows = CSVFormat
                .Builder
                .create()
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader).records

            localFile.delete()
            rows
        } catch (e: Exception) {
            logger.error("Error while reading CSV data for file: $fileName.")
            e.printStackTrace()
            Sentry.captureException(e)
            emptyList()
        }
    }

    fun loadCitiesLocationData(): Map<String, CityLocationData> {
        return try {
            val rows = getRowsFromCSVFile("cities_location.csv")
            val result = mutableMapOf<String, CityLocationData>()
            rows
                .map {
                    val rowData = CityLocationData(
                    city = it.get(0),
                    state = it.get(1),
                    zipcode = it.get(2),
                    country = it.get(3),
                    countryCode = it.get(4),
                    latitude = 0.0,
                    longitude = 0.0,
                    )
                    if (result.containsKey(rowData.city)) {
                        // Same city is appearing twice
                        // So check if the state is different
                        // and then store it with state name
                        val oldValue = result.getValue(rowData.city)
                        if (oldValue.state != rowData.state) {
                            val cityName = "${rowData.city} - (${rowData.state})"
                            result[cityName] = rowData.copy(city = cityName)
                        }
                    } else {
                        result[rowData.city] = rowData
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
