package com.dukaankhata.server.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThirdPartyImageUserDetails(
    val id: String?,
    val name: String?,
    val username: String?,
    val location: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThirdPartyImageUrlDetails(
    val originalUrl: String,
    val smallUrl: String?,
    val thumbnailUrl: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThirdPartyImageDetails(
    val urls: ThirdPartyImageUrlDetails,
    // Optional
    val id: String?,
    val width: Int?,
    val height: Int?,
    val user: ThirdPartyImageUserDetails?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ThirdPartyImageSearchResponse(
    val images: List<ThirdPartyImageDetails>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnsplashImageUserDetails(
    val id: String,
    val name: String?,
    val username: String?,
    val location: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnsplashImageUrlDetails(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnsplashImageDetails(
    val id: String,
    val width: Int,
    val height: Int,
    val blur_hash: String?,
    val urls: UnsplashImageUrlDetails,
    val user: UnsplashImageUserDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnsplashImageSearchResponse(
    val total: Long,
    val total_pages: Long,
    val results: List<UnsplashImageDetails>
)

fun UnsplashImageSearchResponse.toThirdPartyImageSearchResponse(): ThirdPartyImageSearchResponse {
    this.apply {
        return ThirdPartyImageSearchResponse(
            images = results.map { imageDetails ->
                ThirdPartyImageDetails(
                    urls = ThirdPartyImageUrlDetails(
                        // Size of regular(~38KB) << full(~152KB) <<< raw(~990KB)
                        originalUrl = imageDetails.urls.regular ?: imageDetails.urls.full ?: imageDetails.urls.raw ?: error("No original image present for image id: ${imageDetails.id}."),
                        smallUrl = imageDetails.urls.small,
                        thumbnailUrl = imageDetails.urls.thumb
                    ),
                    id = imageDetails.id,
                    width = imageDetails.width,
                    height = imageDetails.height,
                    user = ThirdPartyImageUserDetails(
                        id = imageDetails.user.id,
                        name = imageDetails.user.name,
                        username = imageDetails.user.username,
                        location = imageDetails.user.location,
                    )
                )
            })
    }
}

