package com.server.dk.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ContentType
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
    val mediaType: MediaType = MediaType.IMAGE,
    val mimeType: String?,
    val contentType: ContentType? = ContentType.ACTUAL,
    val mediaQualityType: MediaQualityType? = MediaQualityType.HIGH,
    val lengthInSeconds: Long? = 0, // Only applicable for Video and GIF
    val width: Int? = 0,
    val height: Int? = 0
)

data class MediaDetailsV2(
    val media: List<SingleMediaDetail>
)

val sampleVideoMedia = listOf<MediaDetailsV2>(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/7818bc22-b8e4-4adb-a73f-6c06bfc65b7a.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/93bcc7a3-9f27-4955-ab44-a1cfd739fc9c.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/a0cadb39-bb7f-4f2d-9323-df9601ca2c71.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    ))
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
