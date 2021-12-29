package com.server.common.provider

import com.datastax.oss.driver.api.core.CqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class AddColumnDetails(
    val keyspaceName: String,
    val tableName: String,
    val columnName: String,
    val columnType: String,
)

@Component
class CassandraTableModificationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var session: CqlSession

    @Autowired
    private lateinit var csvDataProvider: CSVDataProvider

    fun addColumn(addColumnDetails: AddColumnDetails) {
        try {
            val query = "ALTER TABLE ${addColumnDetails.keyspaceName}.${addColumnDetails.tableName} ADD ${addColumnDetails.columnName} ${addColumnDetails.columnType}"
            session.execute(query)
            logger.info("Added column $addColumnDetails")
        } catch (e: Exception) {
            logger.error("Error while adding column $addColumnDetails")
            e.printStackTrace()
        }
    }

    // Very Important
    // Always run this locally and comment it afterwards
    fun addNewColumns() {
        logger.info("Start adding new columns")

        val shouldAddColumns = true

        if (shouldAddColumns.not()) {
            logger.error("No need to add columns")
            return
        }
        csvDataProvider.getNewCassandraColumns().map {
            addColumn(it)
        }
    }

}
