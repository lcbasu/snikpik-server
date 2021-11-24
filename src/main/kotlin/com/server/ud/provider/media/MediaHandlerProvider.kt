package com.server.common.provider

import com.server.common.enums.MediaType
import com.server.common.utils.CommonUtils
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.ud.dao.MediaProcessingDetailRepository
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.enums.ResourceType
import com.server.ud.model.BucketAndKey
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
import software.amazon.awssdk.services.rekognition.RekognitionClient
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest
import software.amazon.awssdk.services.rekognition.model.Image
import software.amazon.awssdk.services.rekognition.model.RekognitionException
import software.amazon.awssdk.services.rekognition.model.S3Object


@Component
class MediaHandlerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var mediaProcessingDetailRepository: MediaProcessingDetailRepository

    @Autowired
    private lateinit var awsRekognitionClient: RekognitionClient

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
//            if (mediaDetail.forUser != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
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
//            if (mediaDetail.forUser != request.forUser) error("Media being updated for incorrect user for requestId:${request.fileUniqueId}")
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

    fun getBucketAndKey(singleMediaDetail: SingleMediaDetail): BucketAndKey {
        val bareFile = singleMediaDetail.mediaUrl.replace("http://", "").replace("https://", "")
        val key = bareFile.substring(bareFile.indexOf("/")+1)
        return BucketAndKey(
            bucket = "unboxed-video-ingestion-to-deliver-source71e471f1-1uyj9h1m9ewum",
            key = key
        )
    }

    fun getLabelsForMedia(media: MediaDetailsV2?): Set<String> {
        val labels = mutableSetOf<String>()
        media?.media?.map {
            when (it.mediaType) {
                MediaType.IMAGE -> {
                    val bk = getBucketAndKey(it)
                    labels.addAll(
                        getLabelsForImage(bucket = bk.bucket, imageKeyPath = bk.key)
                    )
                }
                MediaType.VIDEO -> labels.addAll(emptySet())
                MediaType.GIF -> labels.addAll(emptySet())
            }
        }
        return labels
    }

    fun getLabelsForImage(bucket: String, imageKeyPath: String): Set<String> {
        try {
            val s3Object: S3Object = S3Object.builder()
                .bucket(bucket)
                .name(imageKeyPath)
                .build()

            val myImage: Image = Image.builder()
                .s3Object(s3Object)
                .build()

            val detectLabelsRequest: DetectLabelsRequest = DetectLabelsRequest.builder()
                .image(myImage)
                .maxLabels(10)
                .build()

            val labelsResponse = awsRekognitionClient.detectLabels(detectLabelsRequest)
            val labels = labelsResponse.labels()

            // Keep everything that has > 50% confidence
            return labels.filter { it.confidence() > 0.5f }.map {
                it.name()
            }.toSet()
        } catch (e: RekognitionException) {
            e.printStackTrace()
            return emptySet()
        }
    }

    /**
     *
     * TODO: Implement when we have money to support this.
     * https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/rekognition/src/main/java/com/example/rekognition/VideoDetect.java#L98
     *
     */
    fun getLabelsForVideo(bucket: String, videoKeyPath: String): Set<String> {
        try {
            return emptySet()
        } catch (e: RekognitionException) {
            e.printStackTrace()
            return emptySet()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return emptySet()
        }
    }

}
