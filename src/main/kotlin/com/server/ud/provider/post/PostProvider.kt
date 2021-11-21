package com.server.ud.provider.post

import com.github.javafaker.Faker
import com.server.common.entities.MediaProcessingDetail
import com.server.common.enums.ContentType
import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.MediaHandlerProvider
import com.server.common.provider.MediaInputDetail
import com.server.common.provider.RandomIdProvider
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.dk.model.convertToString
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.PaginatedRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.sampleLocationRequests
import com.server.ud.entities.location.Location
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getProfiles
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.LocationFor
import com.server.ud.enums.PostType
import com.server.ud.enums.ResourceType
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.model.convertToString
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.job.JobProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.user.UserV2Provider
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
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var jobProvider: JobProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

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

    fun save(userId: String, request: SavePostRequest) : Post? {
        try {
            var location = request.locationRequest?.let {
                locationProvider.save(userId, it)
            }

            val user = userV2Provider.getUser(userId) ?: error("Missing user for userId: $userId")

            if (location == null && user.userLastLocationId != null && user.userLastLocationZipcode != null) {
                // User location of user for the post
                location = Location(
                    locationId = user.userLastLocationId,
                    createdAt = user.createdAt,
                    userId = user.userId,
                    locationFor = if (request.postType == PostType.GENERIC_POST) LocationFor.GENERIC_POST else LocationFor.COMMUNITY_WALL_POST,
                    zipcode = user.userLastLocationZipcode,
                    googlePlaceId = user.userLastGooglePlaceId,
                    name = user.userLastLocationName,
                    lat = user.userLastLocationLat,
                    lng = user.userLastLocationLng,
                )
            }

            // Assign a random location if location is not present in request and user also has no location
            if (location == null) {
                location = locationProvider.getOrSaveRandomLocation(userId = user.userId, locationFor = if (request.postType == PostType.GENERIC_POST) LocationFor.GENERIC_POST else LocationFor.COMMUNITY_WALL_POST)
            }

            var postId = randomIdProvider.getTimeBasedRandomIdFor(ReadableIdPrefix.PST)

            getPost(postId) ?.let {
                postId += randomIdProvider.getRandomId()
                logger.error("Post already exists for postId: $postId so generating a new Id: $postId")
            }

            val post = Post(
                postId = postId,
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
            postRepository.updateMedia(
                media = media,
                postId = post.postId,
                createdAt = post.createdAt,
                userId = post.userId,
                postType = post.postType
            )
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
                // Assumption 2: Media files are always with USERID/USERID_-_RANDOMID.EXTENSION -> File Unique ID: USERID/RANDOMID

                // Ideally there should be only one video
                val mediaAsset = videoMedia.first()
                val fileInfo = mediaHandlerProvider.getFileInfoFromFilePath(mediaAsset.mediaUrl, true)
                mediaHandlerProvider.saveOrUpdateMediaDetailsAfterSavingResource(
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
                jobProvider.scheduleProcessingForPost(savedPost.postId)
            }
        } else {
            jobProvider.scheduleProcessingForPost(savedPost.postId)
        }
    }

    // Right now only for video
    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        val post = getPost(updatedMediaDetail.resourceId ?: error("Missing resource Id for file: ${updatedMediaDetail.id}")) ?: error("No post found for ${updatedMediaDetail.resourceId} while doing post processing.")
        val exisingMediaList = post.getMediaDetails()?.media ?: emptyList()
        val newMedia = try {
            val otherMediaUrlList = exisingMediaList.filterNot { it.mediaUrl == updatedMediaDetail.inputFilePath }
            otherMediaUrlList + listOf(SingleMediaDetail(
                mediaUrl = updatedMediaDetail.outputFilePath ?: error(" Missing output file path for file: ${updatedMediaDetail.id}"),
                mimeType = "video",
                mediaType = MediaType.VIDEO,
                contentType = ContentType.ACTUAL,
                mediaQualityType = MediaQualityType.HIGH
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            exisingMediaList
        }
        updateMedia(post, MediaDetailsV2(newMedia).convertToString())
        // Now do the post-processing with new media URL
        jobProvider.scheduleProcessingForPost(post.postId)
    }

    fun fakeSave(userId: String, countOfPost: Int): List<Post> {
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
            posts.add(save(userId, req))
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
