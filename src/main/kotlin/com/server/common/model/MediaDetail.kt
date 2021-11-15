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

val sampleVideoMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/90c5f365-6faa-412b-aa2a-65ec4e7b9011.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/91df0202-293e-4d5a-8e81-41aa106ced58.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/USR03D5DB98C4644E3F815F9BFD67_-_5611d9c3-4837-492c-b5a1-4f624dc2e012.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    ))
)

val sampleImageMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/a88f2fb5-9782-40f9-b0b3-a49b1344c324.jpeg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USR03D5DB98C4644E3F815F9BFD67/94b5c46e-54df-4a6c-a658-ddd21865664e.jpeg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    ))
)

val sampleMedia = sampleImageMedia + sampleVideoMedia

fun MediaDetailsV2.convertToString(): String {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            ""
        }
    }
}
