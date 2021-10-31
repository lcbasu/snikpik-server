package com.server.common.client

import com.amazonaws.auth.AWS4Signer
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.server.common.properties.AwsProperties
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate

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
    fun amazonAWSCredentials(): AWSCredentials? {
        return BasicAWSCredentials(
            awsProperties.awsKey,
            awsProperties.awsSecret
        )
    }

    @Bean
    fun awsCredentialsProvider(): AWSStaticCredentialsProvider? {
        return AWSStaticCredentialsProvider(amazonAWSCredentials())
    }

    @Bean
    fun getSigner(): AWS4Signer? {
        val signer = AWS4Signer()
        signer.serviceName = "es"
        signer.regionName = Regions.AP_SOUTH_1.name
        return signer
    }


//    @Bean
//    fun esRestClient(): RestHighLevelClient? {
//        val clientConfiguration: ClientConfiguration = ClientConfiguration.builder()
//            .connectedTo("localhost:9200")
//            .build()
//        val searchClient: RestHighLevelClient = searchClient(serviceName, region)
//
//        return RestClients.create(clientConfiguration).rest()
//    }
//
//    @Bean
//    fun elasticsearchTemplate(): ElasticsearchOperations? {
//        return ElasticsearchRestTemplate(esRestClient()!!)
//    }

    @Bean
    fun getRestHighLevelClient(): RestHighLevelClient {
        val credentialsProvider: CredentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            UsernamePasswordCredentials(awsProperties.es.username, awsProperties.es.password)
        )
        val builder = RestClient.builder(HttpHost(awsProperties.es.host, awsProperties.es.port, awsProperties.es.protocol))
            .setHttpClientConfigCallback { httpClientBuilder: HttpAsyncClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(
                    credentialsProvider
                )
            }
        return RestHighLevelClient(builder);
    }

    @Bean
    fun elasticsearchTemplate(): ElasticsearchOperations? {
        return ElasticsearchRestTemplate(getRestHighLevelClient())
    }

}
