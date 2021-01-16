package com.dukaankhata.server.config

//import com.amazonaws.auth.AWSStaticCredentialsProvider
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.dukaankhata.server.properties.AwsProperties
import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.cloud.aws.mail.simplemail.SimpleEmailServiceJavaMailSender
//import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
//import org.springframework.mail.javamail.JavaMailSender

@Configuration
class AwsMailConfig {

    @Autowired
    var awsProperties: AwsProperties? = null

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
