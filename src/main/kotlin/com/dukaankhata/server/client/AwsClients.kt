package com.dukaankhata.server.client

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.dukaankhata.server.properties.AwsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AwsClients {

    @Autowired
    private lateinit var awsProperties: AwsProperties

    @Bean
    fun getAWSS3Client(): AmazonS3? {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(amazonAWSCredentials()))
            .withRegion(Regions.AP_SOUTH_1)
            .build()
    }

    @Bean
    fun getAWSDynamoDBClient(): AmazonDynamoDB? {
        return AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(amazonAWSCredentials()))
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    awsProperties.dynamoDb.endpoint,
                    Regions.AP_SOUTH_1.name
                )
            )
            .build()
    }

    @Bean
    fun amazonAWSCredentials(): AWSCredentials? {
        return BasicAWSCredentials(
            awsProperties.awsKey,
            awsProperties.awsSecret
        )
    }

    //    @Bean
//    fun amazonSimpleEmailService(): AmazonSimpleEmailService? {
//        return AmazonSimpleEmailServiceClientBuilder.standard()
//                .withCredentials(
//                        AWSStaticCredentialsProvider(
//                                BasicAWSCredentials(
//                                        awsProperties?.awsKey, awsProperties?.awsSecret)))
//                .withRegion(Regions.AP_SOUTH_1)
//                .build()
//    }
//
//    @Bean
//    fun javaMailSender(amazonSimpleEmailService: AmazonSimpleEmailService?): JavaMailSender {
//        return SimpleEmailServiceJavaMailSender(amazonSimpleEmailService)
//    }
}
