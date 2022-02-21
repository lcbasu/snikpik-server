package com.server.ud.provider.post

import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.convertToString
import com.server.common.enums.MediaType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.MediaDetailsV2
import com.server.common.model.convertToString
import com.server.common.model.getSanitizedMediaDetails
import com.server.common.provider.MediaHandlerProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.*
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.post.InstagramPost
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.entities.user.getProfiles
import com.server.ud.entities.user.getSaveLocationRequestFromCurrentLocation
import com.server.ud.entities.user.getSaveLocationRequestFromPermanentLocation
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.LocationFor
import com.server.ud.enums.PostType
import com.server.ud.enums.ResourceType
import com.server.ud.model.AllHashTags
import com.server.ud.model.MediaInputDetail
import com.server.ud.model.convertToString
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.utils.UDCommonUtils
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.regex.Pattern

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
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var postsByUserProvider: PostsByUserProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var deletePostProvider: DeletePostProvider

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

            val user = userV2Provider.getUser(userId) ?: error("Missing user for userId: $userId")

            var locationRequest = request.locationRequest

            if (locationRequest == null) {
               locationRequest = user.getSaveLocationRequestFromCurrentLocation()
            }

            if (locationRequest == null) {
                locationRequest = user.getSaveLocationRequestFromPermanentLocation()
            }

            var location = locationRequest?.let {
                locationProvider.save(userId, it)
            }

            // Assign a random location if location is not present in request and user also has no location
            if (location == null) {
                location = locationProvider.getOrSaveRandomLocation(userId = user.userId, locationFor = if (request.postType == PostType.GENERIC_POST) LocationFor.GENERIC_POST else LocationFor.COMMUNITY_WALL_POST)
            }

            var postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name)

            getPost(postId) ?.let {
                logger.error("Post already exists for postId: $postId so generating a new Id: $postId")
                postId += uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name)
            }

            val post = Post(
                postId = postId,
                userId = user.userId,
                createdAt = request.createdAt ?: DateUtils.getInstantNow(),
                postType = request.postType,
                title = request.title,
                description = request.description,
                media = request.mediaDetails?.getSanitizedMediaDetails()?.convertToString(),
                sourceMedia = request.mediaDetails?.getSanitizedMediaDetails()?.convertToString(),
                tags = AllHashTags(request.tags).convertToString(),
                categories = AllCategoryV2Response(
                    request.categories.map { it.toCategoryV2Response() }
                ).convertToString(),
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
                userProfiles = user.getProfiles().convertToString(),
                locality = location?.locality,
                subLocality = location?.subLocality,
                route = location?.route,
                city = location?.city,
                state = location?.state,
                country = location?.country,
                countryCode = location?.countryCode,
                completeAddress = location?.completeAddress,
            )
            val savedPost = postRepository.save(post)
            // Saving this temporarily with whatever media url is in the source
            // so that user can see all his posts immediately
            postsByUserProvider.save(savedPost)
            handlePostSaved(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMedia(post: Post, media: MediaDetailsV2) {
        try {
            postRepository.save(post.copy(media = media.convertToString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateLabels(post: Post, labels: Set<String>) {
        try {
            val labelsStr = AllLabelsResponse(labels).convertToString()
            postRepository.save(post.copy(labels = labelsStr))
            logger.info("Labels updated for postId: ${post.postId} with labels: $labelsStr")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the for postId: ${post.postId}")
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
                val fileInfo = mediaHandlerProvider.getFileInfoFromFilePath(mediaAsset.mediaUrl)
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
                udJobProvider.scheduleProcessingForPost(savedPost.postId)
            }
        } else {
            udJobProvider.scheduleProcessingForPost(savedPost.postId)
        }
    }

    // Right now only for video
    fun handleProcessedMedia(updatedMediaDetail: MediaProcessingDetail) {
        val post = getPost(updatedMediaDetail.resourceId ?: error("Missing resource Id for file: ${updatedMediaDetail.fileUniqueId}")) ?: error("No post found for ${updatedMediaDetail.resourceId} while doing post processing.")
        val exisingMediaList = post.getMediaDetails().media
        val newMedia = try {
            val toBeReplaceMediaDetail = exisingMediaList.firstOrNull { it.mediaUrl == updatedMediaDetail.inputFilePath }
            toBeReplaceMediaDetail?.let {
                val otherMediaUrlList = exisingMediaList.filterNot { it.mediaUrl == updatedMediaDetail.inputFilePath }
                otherMediaUrlList + listOf(it.copy(mediaUrl = updatedMediaDetail.outputFilePath ?: error(" Missing output file path for file: ${updatedMediaDetail.fileUniqueId}")))
            } ?: exisingMediaList
        } catch (e: Exception) {
            e.printStackTrace()
            exisingMediaList
        }
        updateMedia(post, MediaDetailsV2(newMedia))
        // Now do the post-processing with new media URL
        udJobProvider.scheduleProcessingForPost(post.postId)
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

    fun deletePost(postId: String) {
        val loggedInUserId = securityProvider.validateRequest().getUserIdToUse()
        val post = getPost(postId) ?: error("No post found for postId: $postId")
        if (post.userId != loggedInUserId) {
            error("User $loggedInUserId is not authorized to delete post: $postId. User can only delete their own post.")
        }
        GlobalScope.launch {
            logger.info("Start: Delete post and other dependent information for postId: $postId")
            deletePostProvider.deletePostExpandedData(post, loggedInUserId)
            deleteSinglePost(post)
            logger.info("End: Delete post and other dependent information for postId: $postId")
        }
    }

    private fun deleteSinglePost(post: Post) {
        postRepository.deleteAll(postRepository.findAllByPostId(post.postId))
    }

    fun deletePostFromExplore(postId: String) {
        val loggedInUserId = securityProvider.validateRequest().getUserIdToUse()
        val isAdmin = UDCommonUtils.isAdmin(loggedInUserId)
        val post = getPost(postId) ?: error("No post found for postId: $postId")
        if (isAdmin.not()) {
            error("User $loggedInUserId is not authorized to delete post: $postId. Only admins can delete the post from explore.")
        }
        logger.info("Start: Delete post from explore feed for postId: $postId")
        deletePostProvider.deletePostFromExplore(post)
        logger.info("End: Delete post from explore feed for postId: $postId")
    }

    fun createPost(instagramPost: InstagramPost): Post? {

        val user = userV2Provider.getUser(instagramPost.userId) ?: error("Missing user for userId: ${instagramPost.userId}")
        val location = SaveLocationRequest(
            locationFor = LocationFor.GENERIC_POST,
            zipcode = user.permanentLocationZipcode,
            googlePlaceId = user.permanentGooglePlaceId,
            name = user.permanentLocationName,
            lat = user.permanentLocationLat,
            lng = user.permanentLocationLng,
            locality = user.currentLocationLocality,
            subLocality = user.currentLocationSubLocality,
            route = user.permanentLocationRoute,
            city = user.permanentLocationCity,
            state = user.permanentLocationState,
            country = user.permanentLocationCountry,
            countryCode = user.permanentLocationCountryCode,
        )

        val matches = Pattern.compile("#(\\S+)").matcher(instagramPost.caption ?: "")
        val tags = mutableSetOf<String>()
        while (matches.find()) {
            tags.add(matches.group(1))
        }

        val finalCaption = Pattern
            .compile("/#\\w+\\s*/")
            .matcher(instagramPost.caption ?: "")
            .replaceAll("")

        val req = SavePostRequest(
            postType = PostType.GENERIC_POST,
            title = finalCaption,
            description = instagramPost.caption,
            tags = tags,
            // Figure out a way to get categories using caption and images/videos
            categories = setOf(CategoryV2.ALL),
            locationRequest = location,
            mediaDetails = instagramPost.getMediaDetails(),
            createdAt = instagramPost.createdAt
        )
        return save(instagramPost.userId, req)
    }

}
