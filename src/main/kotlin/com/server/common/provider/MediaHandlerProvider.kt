package com.server.common.provider

import com.cloudinary.Cloudinary
import com.cloudinary.EagerTransformation
import com.cloudinary.utils.ObjectUtils
import com.server.common.controller.ProcessedVideoMessage
import com.server.common.dao.MediaProcessingDetailRepository
import com.server.common.entities.MediaProcessingDetail
import com.server.common.entities.User
import com.server.common.enums.ContentType
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.ud.enums.ResourceType
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.ceil

data class MediaInputDetail(
    var fileUniqueId: String,
    var forUser: String,

    var inputFilePath: String,

    var resourceType: ResourceType,
    var resourceId: String,
)

data class MediaOutputDetail(
    var fileUniqueId: String,
    var forUser: String,

    var outputFilePath: String
)

data class FileInfo (val userId: String, val fileUniqueId: String)

@Component
class MediaHandlerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var cloudinaryClient: Cloudinary

    @Autowired
    private lateinit var mediaProcessingDetailRepository: MediaProcessingDetailRepository

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    private fun checkAndScheduleResourceProcessing(updatedMediaDetail: MediaProcessingDetail) {
        if (updatedMediaDetail.inputFilePresent == true && updatedMediaDetail.outputFilePresent == true) {
            logger.info("Schedule job to do processing of the resource.")
            when (updatedMediaDetail.resourceType ?: error("Resource Type missing for media detail: ${updatedMediaDetail.id}")) {
                ResourceType.POST, ResourceType.WALL -> postProvider.handleProcessedMedia(updatedMediaDetail)
                ResourceType.POST_COMMENT, ResourceType.WALL_COMMENT -> commentProvider.handleProcessedMedia(updatedMediaDetail)
                ResourceType.POST_COMMENT_REPLY, ResourceType.WALL_COMMENT_REPLY -> replyProvider.handleProcessedMedia(updatedMediaDetail)
            }
        }
    }

    fun getMediaProcessingDetail(fileUniqueId: String): MediaProcessingDetail? =
        try {
            mediaProcessingDetailRepository.findById(fileUniqueId).get()
        } catch (e: Exception) {
            null
        }

    fun saveSaveMediaInputDetailRequest(request: MediaInputDetail, addedByUser: User? = null) : MediaProcessingDetail? {
        try {
            val existing = getMediaProcessingDetail(request.fileUniqueId)
            if (existing != null) error("Media details already saved")
            val mediaDetail = MediaProcessingDetail()
            mediaDetail.id = request.fileUniqueId
            mediaDetail.forUser = authProvider.getUser(request.forUser) ?: error("User not found for userId: ${request.forUser}")
            mediaDetail.inputFilePath = request.inputFilePath
            mediaDetail.inputFilePresent = request.inputFilePath.isBlank().not()
            mediaDetail.resourceId = request.resourceId
            mediaDetail.resourceType = request.resourceType
            mediaDetail.addedBy = addedByUser
            return mediaProcessingDetailRepository.save(mediaDetail)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMediaInputDetailRequest(request: MediaInputDetail, addedByUser: User? = null) : MediaProcessingDetail? {
        try {
            val mediaDetail = getMediaProcessingDetail(request.fileUniqueId) ?: error("Media details not present for ${request.fileUniqueId}")
            if (mediaDetail.forUser?.id != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
            mediaDetail.inputFilePath = request.inputFilePath
            mediaDetail.inputFilePresent = request.inputFilePath.isBlank().not()
            mediaDetail.resourceType = request.resourceType
            mediaDetail.resourceId = request.resourceId
            mediaDetail.addedBy = addedByUser
            val updatedMediaDetail = mediaProcessingDetailRepository.save(mediaDetail)
            checkAndScheduleResourceProcessing(updatedMediaDetail)
            return updatedMediaDetail
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun saveSaveMediaOutputDetailRequest(request: MediaOutputDetail) : MediaProcessingDetail? {
        try {
            val existing = getMediaProcessingDetail(request.fileUniqueId)
            if (existing != null) error("Media details already saved")
            val mediaDetail = MediaProcessingDetail()
            mediaDetail.id = request.fileUniqueId
            mediaDetail.forUser = authProvider.getUser(request.forUser) ?: error("User not found for userId: ${request.forUser}")
            mediaDetail.outputFilePath = request.outputFilePath
            mediaDetail.outputFilePresent = request.outputFilePath.isBlank().not()
            return mediaProcessingDetailRepository.save(mediaDetail)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMediaOutputDetailRequest(request: MediaOutputDetail) : MediaProcessingDetail? {
        try {
            val mediaDetail = getMediaProcessingDetail(request.fileUniqueId) ?: error("Media details not present for ${request.fileUniqueId}")
            if (mediaDetail.forUser?.id != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
            mediaDetail.outputFilePath = request.outputFilePath
            mediaDetail.outputFilePresent = request.outputFilePath.isBlank().not()
            val updatedMediaDetail = mediaProcessingDetailRepository.save(mediaDetail)
            checkAndScheduleResourceProcessing(updatedMediaDetail)
            return updatedMediaDetail
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

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

    fun saveOrUpdateMediaDetailsAfterSavingResource(request: MediaInputDetail, addedBy: User? = null) {
        val existing = getMediaProcessingDetail(request.fileUniqueId)
        if (existing == null) {
            saveSaveMediaInputDetailRequest(request, addedBy)
        } else {
            updateMediaInputDetailRequest(request, addedBy)
        }

        logger.info("fileUniqueId: ${request.fileUniqueId}")
        logger.info("userId: ${request.forUser}")
    }

    fun startProcessingAfterVideoProcessing(message: ProcessedVideoMessage?) {

        // "InputFile":
        // "s3://unboxed-video-ingestion-to-deliver-source71e471f1-1uyj9h1m9ewum/assets01
        // /userUploads/USR03D5DB98C4644E3F815F9BFD67/PST423BDFS/7818bc22-b8e4-4adb-a73f-6c06bfc65b7a.mp4"
        val inputFile = message?.InputFile ?:
        error("Input file is required to find what was the resource type of video processed")

        val outputFile = message.Outputs?.processedVideoUrls?.firstOrNull() ?:
        error("Input file is required to find what was the resource type of video processed")

        val fileInfo = getFileInfo(inputFile)

        val existing = getMediaProcessingDetail(fileInfo.fileUniqueId)

        val request = MediaOutputDetail(
            fileUniqueId = fileInfo.fileUniqueId,
            forUser = fileInfo.userId,
            outputFilePath = outputFile,
        )
        if (existing == null) {
            saveSaveMediaOutputDetailRequest(request)
        } else {
            updateMediaOutputDetailRequest(request)
        }
        logger.info("fileUniqueId: ${fileInfo.fileUniqueId}")
        logger.info("userId: ${fileInfo.userId}")
    }

    fun getFileInfo(fileName: String): FileInfo {
        val userStartIndex = fileName.indexOf("USR")
        val userEndIndex = fileName.indexOf("/", userStartIndex)
        val userId = fileName.substring(userStartIndex, userEndIndex)
        val extensionStartIndex = fileName.lastIndexOf(".")

        val fileUniqueId = fileName.substring(userStartIndex, extensionStartIndex)

        return if (fileUniqueId.isNotBlank()) {
            // Video is for Comment
            logger.info("Process media with fileUniqueId: $fileUniqueId")
            FileInfo(userId = userId, fileUniqueId = fileUniqueId)
        } else {
            error("Failed to get uniqueFileId for processed video with fileName: $fileName")
        }
    }

}
