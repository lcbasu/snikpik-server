package com.server.dk.utils

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class CloudUploadDownloadUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var s3Client: AmazonS3

    fun uploadFile(file: File, bucketName: String, prefix: String, fileNameWithExtension: String): String {
        val objectKey = "$prefix/$fileNameWithExtension"
        s3Client.putObject(
            bucketName,
            objectKey,
            file
        )
        val expTime = DateUtils.toDate(DateUtils.dateTimeNow().plusDays(3))
        val generatePreSignedUrlRequest = GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expTime)
        val url = s3Client.generatePresignedUrl(generatePreSignedUrlRequest)
        logger.info("URL: ${url.toExternalForm()}")
        return url.toExternalForm()
    }

}
