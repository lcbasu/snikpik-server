package com.server.dk.provider

import com.server.dk.dto.ThirdPartyImageSearchResponse
import com.server.dk.dto.UnsplashImageSearchResponse
import com.server.dk.dto.toThirdPartyImageSearchResponse
import com.server.dk.properties.UnsplashProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ImageSearchProvider {

    @Autowired
    private lateinit var unsplashProperties: UnsplashProperties

    fun getImagesForQuery(query: String): ThirdPartyImageSearchResponse? {
        val uri = "https://api.unsplash.com/search/photos/?client_id=${unsplashProperties.clientId}&query=$query"
        val restTemplate = RestTemplate()
        val result = restTemplate.getForObject(uri, UnsplashImageSearchResponse::class.java) ?: error("Failed to get images for query: $query")
        return result.toThirdPartyImageSearchResponse()
    }
}
