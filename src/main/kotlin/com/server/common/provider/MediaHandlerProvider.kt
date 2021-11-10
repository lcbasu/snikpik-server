package com.server.common.provider

import com.cloudinary.Cloudinary
import com.cloudinary.EagerTransformation
import com.cloudinary.utils.ObjectUtils
import com.server.common.enums.ContentType
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.ceil


@Component
class MediaHandlerProvider {

    @Autowired
    private lateinit var cloudinaryClient: Cloudinary

    fun uploadFile(file: MultipartFile, mediaType: MediaType): MediaDetailsV2 {
        var fileToUpload: File? = null
        return try {
            fileToUpload = convertMultiPartToFile(file)
            if (mediaType == MediaType.VIDEO) {
                val uploadResult = cloudinaryClient.uploader().upload(
                    fileToUpload,
                    ObjectUtils.asMap(
                        "resource_type", "video",
                        "eager", Arrays.asList(
                            EagerTransformation().streamingProfile("full_hd").format("m3u8")
                        ),
                        "eager_async", true,
                        "public_id", UUID.randomUUID().toString()
                    )
                )
                fileToUpload?.delete()
                MediaDetailsV2(media = listOf(
                    SingleMediaDetail(
                        mediaUrl = uploadResult["url"].toString(),
                        mimeType = uploadResult["format"].toString(),
                        mediaType = mediaType,
                        contentType = ContentType.ACTUAL,
                        mediaQualityType = MediaQualityType.HIGH,
                        lengthInSeconds = ceil(uploadResult["duration"].toString().toDouble()).toLong(),
                        width = uploadResult["width"].toString().toInt(),
                        height = uploadResult["height"].toString().toInt(),
                    )
                ))
            } else {
                val uploadResult = cloudinaryClient.uploader().upload(fileToUpload, ObjectUtils.emptyMap());
                fileToUpload?.delete()
                MediaDetailsV2(media = listOf(
                    SingleMediaDetail(
                        mediaUrl = uploadResult["url"].toString(),
                        mimeType = uploadResult["format"].toString(),
                        mediaType = mediaType,
                        contentType = ContentType.ACTUAL,
                        mediaQualityType = MediaQualityType.HIGH,
                        width = uploadResult["width"].toString().toInt(),
                        height = uploadResult["height"].toString().toInt(),
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fileToUpload?.delete()
            MediaDetailsV2(emptyList())
        }
    }

    fun convertMultiPartToFile(file: MultipartFile): File? {
        val convertedFile = File(file.originalFilename ?: error("Filename is required"))
        val fos = FileOutputStream(convertedFile)
        fos.write(file.bytes)
        fos.close()
        return convertedFile
    }

}
