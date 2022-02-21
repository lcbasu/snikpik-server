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
import com.server.ud.entities.location.Location
import com.server.ud.entities.post.*
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
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.location.ESLocationProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.search.SearchProvider
import com.server.ud.provider.social.FollowersByUserProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import com.server.ud.utils.UDCommonUtils
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.*
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
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var bookmarkProvider: BookmarkProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var searchProvider: SearchProvider

    @Autowired
    private lateinit var postsByZipcodeProvider: PostsByZipcodeProvider

    @Autowired
    private lateinit var postsByUserProvider: PostsByUserProvider

    @Autowired
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider

    @Autowired
    private lateinit var followersByUserProvider: FollowersByUserProvider

    @Autowired
    private lateinit var postsByFollowingProvider: PostsByFollowingProvider

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    @Autowired
    private lateinit var postsByHashTagProvider: PostsByHashTagProvider

    @Autowired
    private lateinit var esLocationProvider: ESLocationProvider

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var nearbyPostsByZipcodeProvider: NearbyPostsByZipcodeProvider

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeProvider: NearbyVideoPostsByZipcodeProvider

    @Autowired
    private lateinit var userActivityProvider: UserActivitiesProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserProvider: BookmarkedPostsByUserProvider

    @Autowired
    private lateinit var likedPostsByUserProvider: LikedPostsByUserProvider

    @Autowired
    private lateinit var zipcodeByPostProvider: ZipcodeByPostProvider


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
            postProcessPostAfterFirstTimeCreationForUser(savedPost)
            handlePostSaved(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
//
//    fun saveUpdatedPost(post: Post) {
//        try {
//            postRepository.save(post)
//            // Re-process all the post related expanded data
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun updateMedia(post: Post, media: MediaDetailsV2) {
        try {
            postRepository.save(post.copy(media = media.convertToString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateLabels(post: Post, labels: Set<String>): Post {
        return try {
            val labelsStr = AllLabelsResponse(labels).convertToString()
            val updatedPost = postRepository.save(post.copy(labels = labelsStr))
            logger.info("Labels updated for postId: ${post.postId} with labels: $labelsStr")
            updatedPost
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the for postId: ${post.postId}")
            post
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
            bookmarkProvider.deletePostExpandedData(post.postId)
            commentProvider.deletePostExpandedData(post.postId)
            likeProvider.deletePostExpandedData(post.postId)
            deletePostExpandedData(post)
            replyProvider.deletePostExpandedData(post.postId)
            searchProvider.deletePostExpandedData(post.postId)
            deleteSinglePost(post)
            logger.info("End: Delete post and other dependent information for postId: $postId")
        }
    }

    fun updatePost(post: Post) {
        GlobalScope.launch {

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
        deletePostFromExplore(post)
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


    // This is to make sure that the new post is immediately available in user posts section
    fun postProcessPostAfterFirstTimeCreationForUser(post: Post) {
        runBlocking {
            val postsByUserFuture = async {
                postsByUserProvider.save(post)
            }

            // Call this ONLY once as calling this multiple times would increase the count of posts for the user
            val postsCountByUserFuture = async {
                postsCountByUserProvider.increasePostCount(post.userId)
            }
            postsByUserFuture.await()
            postsCountByUserFuture.await()
        }
    }

    fun postProcessPostAfterFirstTimeCreation(postId: String) {
        // Update
        // Post By User
        // Post By Zipcode (Nearby Feed)
        // Follower Feed
        // Category Feed
        // Tags Feed
        GlobalScope.launch {
            logger.info("Start postProcessPostAfterFirstTimeCreation: post processing for postId: $postId")
            val post = getPost(postId) ?: error("No post found for $postId while doing post processing.")

            val userActivityFuture = async {
                userActivityProvider.savePostCreationActivity(post)
            }

            val labels = mediaHandlerProvider.getLabelsForMedia(post.getMediaDetails())
            val updatedPost = if (labels.isNotEmpty()) {
                updateLabels(post, labels)
            } else {
                post
            }
            val esLocation = updatedPost.locationId?.let { esLocationProvider.getLocation(it) }
            if (esLocation == null) {
                // Location not processed.
                // Process the location
                locationProvider.processLocation(updatedPost.locationId!!)
            }

            val postsByUserFuture = async {
                postsByUserProvider.save(updatedPost)
            }

            val postsByZipcodeFuture = async {
                postsByZipcodeProvider.save(updatedPost)
            }

            val saveForNearbyPosts = async {
                updatedPost.zipcode?.let {
                    val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(it)
                    nearbyPostsByZipcodeProvider.save(updatedPost, nearbyZipcodes)
                    nearbyVideoPostsByZipcodeProvider.save(updatedPost, nearbyZipcodes)
                    zipcodeByPostProvider.save(updatedPost.postId, nearbyZipcodes)
                }
            }

            val categoriesFeedFuture = async {
                updatedPost.getCategories().categories
                    .map { async { postsByCategoryProvider.save(updatedPost, it.id) } }
                    .map { it.await() }
            }

            val allCategoryFeedFuture = async {
                postsByCategoryProvider.save(updatedPost, CategoryV2.ALL)
            }

            val hashTagsFeedFuture = async {
                updatedPost.getHashTags().tags
                    .map { async { postsByHashTagProvider.save(updatedPost, it) } }
                    .map { it.await() }
            }

//            val savePostToESFuture = async {
//                esPostProvider.save(updatedPost)
//            }

//            val savePostAutoSuggestToESFuture = async {
//                esPostAutoSuggestProvider.save(updatedPost)
//            }

            val algoliaIndexingFuture = async {
                searchProvider.doSearchProcessingForPost(updatedPost)
            }

            userActivityFuture.await()
            postsByUserFuture.await()
            postsByZipcodeFuture.await()
            saveForNearbyPosts.await()
            categoriesFeedFuture.await()
            allCategoryFeedFuture.await()
            hashTagsFeedFuture.await()
//            savePostToESFuture.await()
//            savePostAutoSuggestToESFuture.await()
            algoliaIndexingFuture.await()

            // Schedule Heavy job to be done in isolation
            udJobProvider.scheduleProcessingForPostForFollowers(updatedPost.postId)

            logger.info("Done postProcessPostAfterFirstTimeCreation: post processing for postId: $postId")
        }
    }

    fun processPostForNewNearbyLocation(originalLocation: Location, nearbyZipcodes: Set<String>) {
        GlobalScope.launch {
            logger.info("Start: processPostForNearbyLocation for locationId: ${originalLocation.locationId}")
            if (originalLocation.zipcode == null) {
                logger.error("Location ${originalLocation.name} does not have zipcode. Hence skipping processPostForNearbyLocation")
                return@launch
            }

            val daysToGoInPast = 30L
            val postsPerDayToUse = 50
            val maxSaveListSize = 10

            val nearbyPosts = mutableListOf<NearbyPostsByZipcode>()
            nearbyZipcodes.map { zipcode ->
                PostType.values().map { postType ->
                    // 50 Posts from each date
                    nearbyPosts.addAll(
                        nearbyPostsByZipcodeProvider.getPaginatedFeed(
                            zipCode = zipcode,
                            postType = postType,
                            limit = postsPerDayToUse,
                        ).content?.filterNotNull() ?: emptyList()
                    )
                }
            }
            logger.info("Total ${nearbyPosts.size} nearby post found for the current location ${originalLocation.name}. Save in batches of $maxSaveListSize")
            nearbyPosts.chunked(maxSaveListSize).map {
                nearbyPostsByZipcodeProvider.save(it, originalLocation.zipcode!!)
                val videoPosts = it.mapNotNull { it.toNearbyVideoPostsByZipcode() }
                nearbyVideoPostsByZipcodeProvider.saveWhileProcessing(videoPosts, originalLocation.zipcode!!)
            }
            logger.info("Done: processPostForNearbyLocation for locationId: ${originalLocation.locationId}")
        }
    }

    fun processPostForFollowers(postId: String) {
        // Update
        // Post By User
        // Post By Zipcode (Nearby Feed)
        // Follower Feed
        // Category Feed
        // Tags Feed
        GlobalScope.launch {
            logger.info("Do post processing for postId: $postId for followers of the original poster.")
            val post = getPost(postId) ?: error("No post found for $postId while doing post processing.")
            val userId = post.userId

            var followersResponse = followersByUserProvider.getFeedForFollowersResponse(
                GetFollowersRequest(
                    userId = userId,
                    limit = 5,
                    pagingState = "NOT_SET",
                )
            )
            logger.info("followersResponse: ${followersResponse.followers.size}")
            logger.info(followersResponse.followers.toString())

            val followersFeedFuture: MutableList<Deferred<List<PostsByFollowing?>>> = mutableListOf()

            followersFeedFuture.add(async {
                followersResponse.followers.map {
                    postsByFollowingProvider.save(post, it.followerUserId)
                }
            })

            while (followersResponse.hasNext != null && followersResponse.hasNext == true) {
                followersFeedFuture.add(async {
                    followersResponse.followers.map {
                        postsByFollowingProvider.save(post, it.followerUserId)
                    }
                })

                followersResponse = followersByUserProvider.getFeedForFollowersResponse(
                    GetFollowersRequest(
                        userId = userId,
                        limit = 5,
                        pagingState = followersResponse.pagingState,
                    )
                )

                logger.info("followersResponse: ${followersResponse.followers.size}")
                logger.info(followersResponse.followers.toString())
            }

            followersFeedFuture.map { it.await() }
            logger.info("Followers Post processing completed for postId: $postId for followers of the original poster with userId: $userId.")
        }
    }

    fun deletePostExpandedData(post: Post) {
        GlobalScope.launch {
            logger.info("Start: Delete post and other dependent information for postId: ${post.postId}")

            bookmarkedPostsByUserProvider.deletePostExpandedData(post.postId)
            likedPostsByUserProvider.deletePostExpandedData(post.postId)
            postsByCategoryProvider.deletePostExpandedData(post)
            postsByFollowingProvider.deletePostExpandedData(post.postId)
            postsByHashTagProvider.deletePostExpandedData(post.postId)
            postsByUserProvider.deletePostExpandedData(post.postId)
            postsByZipcodeProvider.deletePostExpandedData(post.postId)
            nearbyPostsByZipcodeProvider.deletePostExpandedData(post)
            nearbyVideoPostsByZipcodeProvider.deletePostExpandedData(post)

            // Only one count has to be decreases as the one post is created by
            // only one user and adds only one count
//            postsCountByUserRepository.decrementPostCount(userId)
            postsCountByUserProvider.decrementPostCount(post.userId)

            logger.info("End: Delete post and other dependent information for postId: ${post.postId}")
        }
    }

    // This is a synchronous call
    fun deletePostFromExplore(post: Post) {
        postsByCategoryProvider.deletePostExpandedData(post)
    }

}
