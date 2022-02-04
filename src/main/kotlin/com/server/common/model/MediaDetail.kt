package com.server.common.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ContentType
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.ud.utils.UDCommonUtils.getFileExtension

const val ALL_MEDIA_SOURCE_BUCKET = "unboxed-video-ingestion-to-deliver-source71e471f1-1uyj9h1m9ewum";
const val ALL_MEDIA_SOURCE_CLOUDFRONT_URL = "https://d2qrqijxy3rkcj.cloudfront.net";
const val PROCESSED_IMAGE_CLOUDFRONT_URL = "https://d1fna9whmio5ul.cloudfront.net";
const val SAMPLE_COVER_IMAGE_URL = "${ALL_MEDIA_SOURCE_CLOUDFRONT_URL}/assets01/AppData/SampleImages/sample-cover-image.jpeg"
const val BEST_M3U8_FILE_SUFFIX = "_Ott_Hls_Ts_Avc_Aac_16x9_1920x1080p_8.5Mbps_qvbr"

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

fun getMediaDetailsFromJsonString(mediaStr: String?): MediaDetailsV2 {
    return try {
        val mediaDetails = jacksonObjectMapper().readValue(mediaStr, MediaDetailsV2::class.java)
        val listOfSingleMediaDetail = mutableListOf<SingleMediaDetail>()
        mediaDetails.media.map { singleMediaDetail ->
            if (singleMediaDetail.mediaType == MediaType.VIDEO && singleMediaDetail.mediaUrl.endsWith(".m3u8")) {
                // Replace the m3u8 url with the best version of m3u8 url
                listOfSingleMediaDetail.add(singleMediaDetail.copy(mediaUrl = getBestM3u8Url(singleMediaDetail.mediaUrl)))
            } else {
                // else let it be as is
                listOfSingleMediaDetail.add(singleMediaDetail)
            }
        }
        MediaDetailsV2(listOfSingleMediaDetail)
    } catch (e: Exception) {
        MediaDetailsV2(emptyList())
    }
}

fun getBestM3u8Url(mediaUrl: String): String {
    val m3u8Url = mediaUrl.replace(".m3u8", "")

    //    val m3u8UrlWithBestQualityExists = try {
//        val url = URL(m3u8UrlWithBestQuality)
//        url.openConnection().getInputStream().close()
//        true
//    } catch (e: Exception) {
//        false
//    }
//
//    return if (m3u8UrlWithBestQualityExists) {
//        m3u8UrlWithBestQuality
//    } else {
//        m3u8Url
//    }

    return "${m3u8Url}${BEST_M3U8_FILE_SUFFIX}.m3u8"
}

fun getMediaUrlForNotification(mediaStr: String?): String {
    val singleMediaDetail = getMediaDetailsFromJsonString(mediaStr).media.firstOrNull() ?: return ""
    if (singleMediaDetail.mediaType == MediaType.VIDEO && singleMediaDetail.mediaUrl.endsWith(".m3u8")) {
        return try {
            // Send the thumbnail URL for processed video media
            val mediaUrl = singleMediaDetail.mediaUrl
            val fileName = mediaUrl.substring(mediaUrl.lastIndexOf("USR")).replace(".m3u8", "")
            val userId = fileName.split("_-_")[0]
            val key = "assets01/userUploads/${userId}/${fileName}_thumbnail.0000000.jpg"
            "${ALL_MEDIA_SOURCE_CLOUDFRONT_URL}/${key}"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    // For non-HLS video, we will return the media url
    return singleMediaDetail.mediaUrl
}

val sampleVideoMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_d960ceab-f83a-4581-aa9d-0af2f231bd45.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 2160,
            height = 3840,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_aca6ad85-4a8f-4bb2-a597-afad052d8a95.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 2160,
            height = 3840,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_992b56d6-6184-45c9-8542-6611c2a2d514.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 3840,
            height = 2160,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_96de4124-b01b-4002-9d63-f2c285d3be03.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 2160,
            height = 3840,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_93a43e2b-7fae-442a-b451-cd2e6e7c4eaf.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 1080,
            height = 1920,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2_-_30b32cf0-9a18-46a0-8757-a336ba9529c4.mp4",
            mimeType = "video",
            mediaType = MediaType.VIDEO,
            width = 2160,
            height = 4096,
            mediaQualityType = MediaQualityType.HIGH,
        )
    ))
)

val sampleImageMedia = listOf(
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3222,
            height = 2327,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-daria-shevtsova-1029803.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3024,
            height = 4032,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-dmitry-zvolskiy-2082087.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 7320,
            height = 4885,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-dominika-roseclay-1139784.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3648,
            height = 5168,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-ksenia-chernaya-5806989.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 2537,
            height = 3800,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-ksenia-chernaya-6021777.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 2670,
            height = 4000,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-mister-mister-2442904.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3024,
            height = 4032,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-pixabay-462235.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 1333,
            height = 2000,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-rachel-claire-5865687.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3648,
            height = 5291,
            mediaQualityType = MediaQualityType.HIGH,
        )
    )),
    MediaDetailsV2(listOf(
        SingleMediaDetail(
            mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-vecislavas-popa-1571460.jpg",
            mimeType = "image",
            mediaType = MediaType.IMAGE,
            width = 3400,
            height = 2186,
            mediaQualityType = MediaQualityType.HIGH,
        )
    ))
)

val sampleMedia = (sampleImageMedia + sampleVideoMedia).shuffled()

fun MediaDetailsV2.convertToString(): String {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            ""
        }
    }
}

fun MediaDetailsV2.getSanitizedMediaDetails(): MediaDetailsV2 {
    this.apply {
        return try {
            MediaDetailsV2(this.media.map {
                val extension = getFileExtension(it.mediaUrl)
                if (validVideoExtensions.contains(".$extension") && it.mediaType != MediaType.VIDEO) {
                    it.copy(mediaType = MediaType.VIDEO, mimeType = "video")
                } else {
                    it
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            this
        }
    }
}

val validVideoExtensions = listOf(
    ".3g2",
    ".3gp",
    ".264",
    ".265",
    ".a64",
    ".apng",
    ".asf",
    ".avi",
    ".avs",
    ".avs2",
    ".cavs",
    ".f4v",
    ".flm",
    ".flv",
    ".gif",
    ".gxf",
    ".h261",
    ".h263",
    ".h264",
    ".h265",
    ".hevc",
    ".ismv",
    ".ivf",
    ".m1v",
    ".m2v",
    ".m4v",
    ".mjpeg",
    ".mjpg",
    ".mkv",
    ".mov",
    ".mp4",
    ".mpeg",
    ".mpeg4",
    ".mpg",
    ".ogv",
    ".rm",
    ".vc1",
    ".vc2",
    ".vob",
    ".webm",
    ".wmv",
    ".y4m",
    ".m3u8",
)
