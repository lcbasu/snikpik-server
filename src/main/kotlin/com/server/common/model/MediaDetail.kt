package com.server.dk.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType

data class MediaDetail(
    val mediaUrl: String,
    val mimeType: String
)

data class MediaDetails(
    val media: List<MediaDetail>
)

fun MediaDetails.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

data class SingleMediaDetail(
    val mediaUrl: String,
    val mimeType: String,
    val mediaType: MediaType? = MediaType.IMAGE,
    val mediaQualityType: MediaQualityType? = MediaQualityType.HIGH,
    val lengthInSeconds: Long? = 0, // Only applicable for Video and GIF
    val width: Int?,
    val height: Int?
)

data class MediaDetailsV2(
    val media: List<SingleMediaDetail>
)

fun MediaDetailsV2.convertToString(): String {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            ""
        }
    }
}
