package com.server

import com.server.dk.properties.*
//import org.socialsignin.spring.data.dynamodb.repository.EnableScan
//import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableConfigurationProperties(
	SecurityProperties::class,
	AwsProperties::class,
	PaymentProperties::class,
	TwilioProperties::class,
	UnsplashProperties::class,
	PdfProperties::class)
@EnableSwagger2
//@EnableJpaRepositories(excludeFilters = [
//	ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [EnableScan::class])
//])
//@EnableDynamoDBRepositories(includeFilters = [
//	ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [EnableScan::class])
//])
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
