package com.dukaankhata.server

import com.dukaankhata.server.properties.AwsProperties
import com.dukaankhata.server.properties.PaymentProperties
import com.dukaankhata.server.properties.SecurityProperties
import com.dukaankhata.server.properties.TwilioProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
		SecurityProperties::class,
		AwsProperties::class,
		PaymentProperties::class,
		TwilioProperties::class)
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
