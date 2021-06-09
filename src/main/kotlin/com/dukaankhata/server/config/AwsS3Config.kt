package com.dukaankhata.server.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.dukaankhata.server.properties.AwsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AwsS3Config {

    @Autowired
    private lateinit var awsProperties: AwsProperties

    @Bean
    fun getAWSS3Client(): AmazonS3? {
        val credentials = BasicAWSCredentials(
            awsProperties.awsKey,
            awsProperties.awsSecret
        )
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build()
    }
}
