package com.server.ud.provider.post

import com.github.javafaker.Faker
import com.server.common.entities.MediaProcessingDetail
import com.server.common.enums.ContentType
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.MediaHandlerProvider
import com.server.common.provider.MediaInputDetail
import com.server.common.provider.UniqueIdProvider
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.dk.model.convertToString
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.PaginatedRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.sampleLocationRequests
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getProfiles
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.enums.ResourceType
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.model.convertToString
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.location.LocationProvider
import com.server.ud.service.post.ProcessPostSchedulerService
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
class PostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var processPostSchedulerService: ProcessPostSchedulerService

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    fun getPost(postId: String): Post? =
        try {
            val posts = postRepository.findAllByPostId(postId)
            if (posts.size > 1) {
                error("More than one post has same postId: $postId")
            }
            posts.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Post for $postId failed.")
            e.printStackTrace()
            null
        }

    fun save(user: UserV2, request: SavePostRequest) : Post? {
        try {
            val location = request.locationRequest?.let {
                locationProvider.save(user, it)
            }
            val post = Post(
                postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name),
                userId = user.userId,
                createdAt = Instant.now(),
                postType = request.postType,
                title = request.title,
                description = request.description,
                media = request.mediaDetails?.convertToString(),
                tags = request.tags.convertToString(),
                categories = request.categories.joinToString(","),
                locationId = location?.locationId,
                zipcode = location?.zipcode,
                locationLat = location?.lat,
                locationLng = location?.lng,
                locationName = location?.name,
                googlePlaceId = location?.googlePlaceId,
                userName = user.fullName,
                userMobile = user.absoluteMobile,
                userHandle = user.handle,
                userCountryCode = user.countryCode,
                userProfile = user.getProfiles().firstOrNull()
            )
            val savedPost = postRepository.save(post)
            handlePostSaved(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMedia(post: Post, media: String) {
        try {
            postRepository.updateMedia(post.postId, media)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handlePostSaved(savedPost: Post) {

        val videoMedia = savedPost.getMediaDetails()?.media?.filter { it.mediaType == MediaType.VIDEO } ?: emptyList()
        // Only one video has to be present for processing
        // Otherwise the flow breaks for now
        // Figure out a better way in future
        if (videoMedia.size == 1) {
            try {
                // Do media processing
                // CRITICAL:
                // Assumption 1: Right now we are assuming only one VIDEO asset being uploaded
                // Assumption 2: Media files are always with USERID/RANDOMID.EXTENSION -> File Unique ID: USERID/RANDOMID

                // Ideally there should be only one video
                val mediaAsset = videoMedia.first()
                val fileInfo = mediaHandlerProvider.getFileInfo(mediaAsset.mediaUrl)
                mediaHandlerProvider.saveMediaDetailsAfterSavingResource(
                    MediaInputDetail(
                        fileUniqueId = fileInfo.fileUniqueId,
                        forUser = fileInfo.userId,
                        inputFilePath = mediaAsset.mediaUrl,
                        resourceType = ResourceType.POST,
                        resourceId = savedPost.postId,
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to normal processing
                processPostSchedulerService.createPostProcessingJob(savedPost)
            }
        } else {
            processPostSchedulerService.createPostProcessingJob(savedPost)
        }
    }

    // Right now only for video
    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        val post = getPost(updatedMediaDetail.resourceId ?: error("Missing resource Id for file: ${updatedMediaDetail.id}")) ?: error("No post found for ${updatedMediaDetail.resourceId} while doing post processing.")
        try {
            val exisingMediaList = post.getMediaDetails()?.media ?: emptyList()
            val otherMediaUrlList = exisingMediaList.filterNot { it.mediaUrl == updatedMediaDetail.inputFilePath }
            val newMedia = otherMediaUrlList + listOf(SingleMediaDetail(
                mediaUrl = updatedMediaDetail.outputFilePath ?: error(" Missing output file path for file: ${updatedMediaDetail.id}"),
                mimeType = "video",
                mediaType = MediaType.VIDEO,
                contentType = ContentType.ACTUAL,
                mediaQualityType = MediaQualityType.HIGH
            ))
            updateMedia(post, MediaDetailsV2(newMedia).convertToString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Now do the post-processing with new media URL
        processPostSchedulerService.createPostProcessingJob(post)
    }

    fun fakeSave(user: UserV2, countOfPost: Int): List<Post> {
        val posts = mutableListOf<Post?>()
        for (i in 1..countOfPost) {
            val faker = Faker()
            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher(),
                tags = HashTagsList(listOf(
                    HashTagData(
                        tagId = "newhouse",
                        displayName = "newhouse",
                    ),
                    HashTagData(
                        tagId = "lakesideview",
                        displayName = "lakesideview",
                    )
                )),
                categories = setOf(CategoryV2.KITCHEN, CategoryV2.EXTERIOR),
                locationRequest = sampleLocationRequests[Random.nextInt(sampleLocationRequests.size)]
            )
            posts.add(save(user, req))
        }
        return posts.filterNotNull()
    }

    fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>? {
        return getPageOfPosts(request.limit, request.pagingState)
    }

    fun getPageOfPosts(limit: Int, pagingState: String?): CassandraPageV2<Post?>? {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        return getPageOfUsers(pageRequest)
    }

    fun getPageOfUsers(cassandraPageRequest: CassandraPageRequest?): CassandraPageV2<Post?>? {
        val userSlice = postRepository.findAll(cassandraPageRequest as Pageable)
        return CassandraPageV2(userSlice)
    }

}
