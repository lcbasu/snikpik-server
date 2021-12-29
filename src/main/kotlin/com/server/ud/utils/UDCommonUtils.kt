package com.server.ud.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UDCommonUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    const val randomLocationId = "LOC_RANDOM"
    const val randomLocationZipcode = "ZZZZZZ"
    const val randomLocationName = "Global"

    fun getFileExtension(fileUrl: String): String {
        return try {
            val extensionStartIndex = fileUrl.lastIndexOf(".")
            fileUrl.substring(extensionStartIndex + 1)
        } catch (e: Exception) {
            logger.error("Error while getting file extension for fileUrl: $fileUrl")
            ""
        }
    }

}

