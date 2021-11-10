package com.server.common.client

import com.cloudinary.Cloudinary
import com.server.common.properties.CloudinaryProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CloudinaryClient {

    @Autowired
    private lateinit var cloudinaryProperties: CloudinaryProperties

    @Bean
    fun getCloudinaryClient(): Cloudinary? {
        val config = mutableMapOf<String, String>()
        config["cloud_name"] = cloudinaryProperties.cloudName
        config["api_key"] = cloudinaryProperties.apiKey
        config["api_secret"] = cloudinaryProperties.apiSecret
        return Cloudinary(config)
    }

}
