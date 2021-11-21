package com.server.common.provider

import com.server.common.utils.CommonUtils
import com.server.ud.dao.MediaProcessingDetailRepository
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.enums.ResourceType
import com.server.ud.model.FileInfo
import com.server.ud.model.MediaInputDetail
import com.server.ud.model.MediaOutputDetail
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MediaHandlerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var mediaProcessingDetailRepository: MediaProcessingDetailRepository

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    private fun checkAndScheduleResourceProcessing(updatedMediaDetail: MediaProcessingDetail) {
        if (updatedMediaDetail.inputFilePresent == true && updatedMediaDetail.outputFilePresent == true) {
            logger.info("Schedule job to do processing of the resource.")
            when (updatedMediaDetail.resourceType ?: error("Resource Type missing for media detail: ${updatedMediaDetail.fileUniqueId}")) {
                ResourceType.POST, ResourceType.WALL -> postProvider.handleProcessedMedia(updatedMediaDetail)
                ResourceType.POST_COMMENT, ResourceType.WALL_COMMENT -> commentProvider.handleProcessedMedia(updatedMediaDetail)
                ResourceType.POST_COMMENT_REPLY, ResourceType.WALL_COMMENT_REPLY -> replyProvider.handleProcessedMedia(updatedMediaDetail)
            }
        }
    }

    fun getMediaProcessingDetail(fileUniqueId: String): MediaProcessingDetail? =
        try {
            val media = mediaProcessingDetailRepository.findAllByFileUniqueId(fileUniqueId)
            if (media.size > 1) {
                error("More than one media details have has same fileUniqueId: $fileUniqueId")
            }
            media.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting MediaProcessingDetail for fileUniqueId: $fileUniqueId failed.")
            e.printStackTrace()
            null
        }

    fun saveSaveMediaInputDetailRequest(request: MediaInputDetail) : MediaProcessingDetail? {
        try {
            val existing = getMediaProcessingDetail(request.fileUniqueId)
            if (existing != null) error("Media details already saved")
            val mediaDetail = MediaProcessingDetail()
            mediaDetail.fileUniqueId = request.fileUniqueId
            mediaDetail.forUser = null
            mediaDetail.inputFilePath = request.inputFilePath
            mediaDetail.inputFilePresent = request.inputFilePath.isBlank().not()
            mediaDetail.resourceId = request.resourceId
            mediaDetail.resourceType = request.resourceType
            return mediaProcessingDetailRepository.save(mediaDetail)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMediaInputDetailRequest(request: MediaInputDetail) : MediaProcessingDetail? {
        try {
            val mediaDetail = getMediaProcessingDetail(request.fileUniqueId) ?: error("Media details not present for ${request.fileUniqueId}")
            if (mediaDetail.forUser != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
            mediaDetail.inputFilePath = request.inputFilePath
            mediaDetail.inputFilePresent = request.inputFilePath.isBlank().not()
            mediaDetail.resourceType = request.resourceType
            mediaDetail.resourceId = request.resourceId
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
            mediaDetail.fileUniqueId = request.fileUniqueId
            mediaDetail.forUser = null
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
            if (mediaDetail.forUser != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
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

    fun saveOrUpdateMediaDetailsAfterSavingResource(request: MediaInputDetail) {
        val existing = getMediaProcessingDetail(request.fileUniqueId)
        if (existing == null) {
            saveSaveMediaInputDetailRequest(request)
        } else {
            updateMediaInputDetailRequest(request)
        }

        logger.info("fileUniqueId: ${request.fileUniqueId}")
        logger.info("userId: ${request.forUser}")
    }

    fun startProcessingAfterVideoProcessing(processedVideoUrls : Set<String>) {

        // "InputFile":
        // "s3://unboxed-video-ingestion-to-deliver-source71e471f1-1uyj9h1m9ewum/assets01
        // /userUploads/USR03D5DB98C4644E3F815F9BFD67/USR03D5DB98C4644E3F815F9BFD67_-_1340df61-b9e8-493d-a2d4-3cb61a2772a4.mp4"

        processedVideoUrls.map {
            val fileInfo = getFileInfoFromFilePath(it, false)

            val existing = getMediaProcessingDetail(fileInfo.fileUniqueId)

            val request = MediaOutputDetail(
                fileUniqueId = fileInfo.fileUniqueId,
                forUser = fileInfo.userId,
                outputFilePath = it,
            )
            if (existing == null) {
                saveSaveMediaOutputDetailRequest(request)
            } else {
                updateMediaOutputDetailRequest(request)
            }
            logger.info("fileUniqueId: ${fileInfo.fileUniqueId}")
            logger.info("userId: ${fileInfo.userId}")
        }
    }

    fun getFileInfoFromFilePath(fileName: String, isInput: Boolean): FileInfo {
        val userStartIndex = if (isInput) fileName.lastIndexOf("USR") else fileName.indexOf("USR")
        val extensionStartIndex = fileName.lastIndexOf(".")
        val fileUniqueId = fileName.substring(userStartIndex, extensionStartIndex)
        val (userId, fileId) = fileUniqueId.split(CommonUtils.STRING_SEPARATOR)
        return if (fileUniqueId.isNotBlank()) {
            // Video is for Comment
            logger.info("Get media details from fileUniqueId: $fileUniqueId")
            FileInfo(userId = userId, fileUniqueId = fileUniqueId)
        } else {
            error("Failed to get uniqueFileId for processed video with fileName: $fileName")
        }
    }

}
