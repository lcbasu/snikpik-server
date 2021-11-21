package com.server.dk.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ContentType
import com.server.ud.enums.MediaPresenceType
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
    val mimeType: String? = null,
    val contentType: ContentType? = ContentType.ACTUAL,
    val mediaQualityType: MediaQualityType? = MediaQualityType.HIGH,
    val lengthInSeconds: Long? = 0, // Only applicable for Video and GIF
    val width: Int? = 0,
    val height: Int? = 0
)

data class MediaDetailsV2(
    val media: List<SingleMediaDetail> = emptyList()
)

val sampleVideoMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_d960ceab-f83a-4581-aa9d-0af2f231bd45.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_aca6ad85-4a8f-4bb2-a597-afad052d8a95.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_992b56d6-6184-45c9-8542-6611c2a2d514.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_96de4124-b01b-4002-9d63-f2c285d3be03.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_93a43e2b-7fae-442a-b451-cd2e6e7c4eaf.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_30b32cf0-9a18-46a0-8757-a336ba9529c4.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO
        )
    ))
)

val sampleImageMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-daria-shevtsova-1029803.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-dmitry-zvolskiy-2082087.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-dominika-roseclay-1139784.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-ksenia-chernaya-5806989.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-ksenia-chernaya-6021777.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-mister-mister-2442904.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-pixabay-462235.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-rachel-claire-5865687.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-vecislavas-popa-1571460.jpg",
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

fun getMediaPresenceType (media: MediaDetailsV2?): MediaPresenceType {
    return media?.let {
        MediaPresenceType.NO_MEDIA
        when {
            media.media.isEmpty() -> MediaPresenceType.NO_MEDIA
            media.media.isNotEmpty() -> {
                val groupedByType = media.media.groupBy { it.mediaType }
                if (groupedByType.size == 1 && groupedByType.containsKey(MediaType.IMAGE)) {
                    MediaPresenceType.ONLY_IMAGE
                } else if (groupedByType.size == 1 && groupedByType.containsKey(MediaType.GIF)) {
                    MediaPresenceType.ONLY_GIF
                }  else if (groupedByType.size == 1 && groupedByType.containsKey(MediaType.VIDEO)) {
                    MediaPresenceType.ONLY_VIDEO
                } else {
                    MediaPresenceType.MIXED
                }
            }
            else -> MediaPresenceType.NO_MEDIA
        }
    } ?: MediaPresenceType.NO_MEDIA
}
