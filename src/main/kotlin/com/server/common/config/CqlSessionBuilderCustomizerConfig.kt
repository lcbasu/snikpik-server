package com.server.common.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.server.common.properties.DatastaxProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStream

@Configuration
class CqlSessionBuilderCustomizerConfig {

    @Autowired
    private lateinit var properties: DatastaxProperties

    @Autowired
    private lateinit var s3Client: AmazonS3

    @Bean
    fun sessionBuilderCustomizer(): CqlSessionBuilderCustomizer? {
        val s3Object = s3Client.getObject(GetObjectRequest(properties.astra.secureConnectBundleS3Bucket, properties.astra.secureConnectBundleS3key))
        val objectData: InputStream = s3Object.objectContent
        return CqlSessionBuilderCustomizer {
                builder: CqlSessionBuilder -> builder.withCloudSecureConnectBundle(objectData)
        }
    }
}
