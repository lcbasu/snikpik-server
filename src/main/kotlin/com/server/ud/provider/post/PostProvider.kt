package com.server.ud.provider.post

import com.server.common.dto.AllCategoryV2Response
import com.server.common.dto.AllLabelsResponse
import com.server.common.dto.convertToString
import com.server.common.dto.toCategoryV2Response
import com.server.common.enums.MediaType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.convertToString
import com.server.common.model.getSanitizedMediaDetails
import com.server.common.provider.MediaHandlerProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils
import com.server.common.utils.DateUtils
import com.server.shop.provider.PostTaggedProductsProvider
import com.server.ud.dao.post.PostReportByUserRepository
import com.server.ud.dao.post.PostRepository
import com.server.ud.dao.post.TrackingByPostRepository
import com.server.ud.dto.*
import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.location.Location
import com.server.ud.entities.post.*
import com.server.ud.entities.user.getProfiles
import com.server.ud.entities.user.getSaveLocationRequestFromCurrentLocation
import com.server.ud.entities.user.getSaveLocationRequestFromPermanentLocation
import com.server.ud.enums.*
import com.server.ud.model.AllHashTags
import com.server.ud.model.MediaInputDetail
import com.server.ud.model.convertToString
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.automation.AutomationProvider
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
    private lateinit var postsByPostTypeProvider: PostsByPostTypeProvider

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

    @Autowired
    private lateinit var trackingByPostRepository: TrackingByPostRepository

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    @Autowired
    private lateinit var postReportByUserRepository: PostReportByUserRepository

    @Autowired
    private lateinit var postTaggedProductsProvider: PostTaggedProductsProvider

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

            var postId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.PST.name)

            getPost(postId) ?.let {
                logger.error("Post already exists for postId: $postId so generating a new Id: $postId")
                postId += uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.PST.name)
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
                categories = CommonUtils.convertToStringBlob(AllCategoryV2Response(
                        request.categories.map { it.toCategoryV2Response() }
                    )),
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
            processJustAfterCreation(savedPost, request.taggedProductIds ?: emptySet())
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun processJustAfterCreation(savedPost: Post, taggedProductIds: Set<String> = emptySet()) {
        GlobalScope.launch {

            // Saving this temporarily with whatever media url is in the source
            // so that user can see all his posts immediately
            postProcessPostAfterFirstTimeCreationForUser(savedPost)
            handlePostSaved(savedPost)
            postTaggedProductsProvider.saveTaggedProduct(savedPost.postId, taggedProductIds)

            automationProvider.sendSlackMessageForNewPost(savedPost)
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

    fun deleteOlderPosts(zipcodes: Set<String>, postType: PostType, postId: String) {
        logger.info("Deleting older posts for zipcodes: $zipcodes, postType: $postType, postId: $postId")
        nearbyPostsByZipcodeProvider.deleteOldPosts(zipcodes, postType, postId)
        nearbyVideoPostsByZipcodeProvider.deleteOldPosts(zipcodes, postType, postId)
        logger.info("Deleted older posts for zipcodes: $zipcodes, postType: $postType, postId: $postId")
    }

    fun deletePost(postId: String) {
        val loggedInUserId = securityProvider.validateRequest().getUserIdToUse()
        val post = getPost(postId)// ?: error("No post found for postId: $postId")
        if (post != null) {
            // If the post is not null and the user is not authorized to delete the post, then we will not delete the post
            if (post.userId != loggedInUserId) {
                error("User $loggedInUserId is not authorized to delete post: $postId. User can only delete their own post.")
            }
            automationProvider.sendSlackMessageForPostDeletion(post)
            postsCountByUserProvider.decrementPostCount(post.userId)
        }
        // Case 1: If the post is null then it means we failed to delete the post last time so cleanup right now
        // Case 1: If the post is not null then it means the post exists so delete anyways
        // Hence in both the cases the cascading change needs to continue
        // Remove this feature after sometime once we are sure that none of the older post needs cleanup.
        // Do cleanup inside post != null and enable error("No post found for postId: $postId")

        // Repeat after avery 6 minutes for 1 hour
        udJobProvider.schedulePostDeletion(postId, 6 * 60, 10)
    }

    fun deletePostWithId(postId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete post and other dependent information for postId: $postId")

            async { postsByCategoryProvider.deletePostExpandedDataWithPostId(postId) }
            async { postsByPostTypeProvider.deletePostExpandedDataWithPostId(postId) }
            async { bookmarkedPostsByUserProvider.deletePostExpandedData(postId) }
            async { likedPostsByUserProvider.deletePostExpandedData(postId) }
            async { postsByFollowingProvider.deletePostExpandedData(postId) }
            async { postsByHashTagProvider.deletePostExpandedData(postId) }
            async { postsByUserProvider.deletePostExpandedData(postId) }
            async { postsByZipcodeProvider.deletePostExpandedData(postId) }
            async { nearbyPostsByZipcodeProvider.deletePostExpandedData(postId) }
            async { nearbyVideoPostsByZipcodeProvider.deletePostExpandedData(postId) }

            // Only one count has to be decreases as the one post is created by
            // only one user and adds only one count
//            postsCountByUserRepository.decrementPostCount(userId)
            async { deletePostExpandedData(postId) }
            async { deleteSinglePost(postId) }

            logger.info("End: Delete post and other dependent information for postId: $postId")
        }
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
        // Forum Feed
        // Tags Feed
        GlobalScope.launch {
            logger.info("Start postProcessPostAfterFirstTimeCreation: post processing for postId: $postId")
            val post = getPost(postId) ?: error("No post found for $postId while doing post processing.")

            val userActivityFuture = async {
                userActivityProvider.savePostCreationActivity(post)
            }

            val labels = mediaHandlerProvider.getLabelsForMedia(post.getMediaDetails())
            val updatedPost = if (labels.isNotEmpty()) {
                val update = updateLabels(post, labels, ProcessingType.NO_PROCESSING)
                update.newPost ?: update.oldPost
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

            val postsByZipcodeFuture = async { postsByZipcodeProvider.processPostExpandedData(updatedPost) }

            val nearbyPostsByZipcodeFuture = async { nearbyPostsByZipcodeProvider.processPostExpandedData(updatedPost) }
            val nearbyVideoPostsByZipcodeFuture =
                async { nearbyVideoPostsByZipcodeProvider.processPostExpandedData(updatedPost) }
            zipcodeByPostProvider.processPostExpandedData(updatedPost)

            val postsByCategoryFuture = async { postsByCategoryProvider.processPostExpandedData(updatedPost) }
            val postsByPostTypeFuture = async { postsByPostTypeProvider.processPostExpandedData(updatedPost) }

            val postsByHashTagFuture = async { postsByHashTagProvider.processPostExpandedData(updatedPost) }

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
            postsByCategoryFuture.await()
            postsByPostTypeFuture.await()
            postsByHashTagFuture.await()
            postsByZipcodeFuture.await()
            nearbyPostsByZipcodeFuture.await()
            nearbyVideoPostsByZipcodeFuture.await()
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

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            logger.info("Start deletePostExpandedData: Delete post and other dependent information for postId: $postId")
            async { bookmarkProvider.deleteResourceExpandedData(postId) }
            async { commentProvider.deletePostExpandedData(postId) }
            async { likeProvider.deleteResourceExpandedData(postId) }
            async { replyProvider.deletePostExpandedData(postId) }
            async { searchProvider.deletePostExpandedData(postId) }
            async { userActivityProvider.deletePostExpandedData(postId) }
            logger.info("End deletePostExpandedData: Delete post and other dependent information for postId: $postId")
        }
    }

    // This is a synchronous call
    fun deletePostFromExplore(post: Post) {
        postsByCategoryProvider.deletePostExpandedData(post)
    }

    fun trackPost(postId: String, postTrackerType: PostTrackerType, trackingId: String) {
        GlobalScope.launch {
//            when (postTrackerType) {
//                PostTrackerType.POST_COMMENT_REPLY -> TODO()
//                PostTrackerType.POST_COMMENT -> TODO()
//                PostTrackerType.POST_BOOKMARK -> TODO()
//                PostTrackerType.POST_LIKE -> TODO()
//                PostTrackerType.POST_ZIPCODE -> TODO()
//                PostTrackerType.POST_CATEGORY -> TODO()
//                PostTrackerType.POST_USER_FOLLOWER -> TODO()
//                PostTrackerType.POST_USER_FOLLOWING -> TODO()
//                PostTrackerType.POST_HASH_TAG -> TODO()
//            }

            val savedTracking = trackingByPostRepository.save(TrackingByPost(
                postId = postId,
                trackingId = trackingId,
                postTrackerType = postTrackerType,
            ))
            logger.info("Saved tracking: ${savedTracking.postId} ${savedTracking.postTrackerType} ${savedTracking.trackingId}")
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

    private fun deleteSinglePost(postId: String) {
        postRepository.deleteByPostId(postId)
    }

    fun updatePostAlgoliaData() {
        postRepository.findAll().filterNotNull().filter { it.postType == PostType.GENERIC_POST }.map {
            logger.info("Updating algolia for postId: ${it.postId}")
            searchProvider.doSearchProcessingForPost(it)
        }
    }

    fun updateSourceMediaForAll() {
        postRepository.findAll().filterNotNull().filter { it.sourceMedia == null }.map {
            logger.info("Updating source media for postId: ${it.postId}")
            val media = it.getMediaDetails()
            val videoMediaList = it.getMediaDetails().media.filter { it.mediaType == MediaType.VIDEO }
            if (videoMediaList.size == 1) {
                val videoMedia = videoMediaList.first()
                val fileInfo = mediaHandlerProvider.getFileInfoFromFilePath(videoMedia.mediaUrl)
                val existing = mediaHandlerProvider.getMediaProcessingDetail(fileInfo.fileUniqueId)
                if (existing != null &&
                    existing.inputFilePresent == true &&
                    existing.inputFilePath != null &&
                    existing.outputFilePresent == true &&
                    existing.outputFilePath == videoMedia.mediaUrl) {
                    val sourceMedia = MediaDetailsV2(
                        media = listOf(
                            SingleMediaDetail(
                                mediaUrl = existing.inputFilePath!!,
                                mimeType = "video",
                                mediaType = MediaType.VIDEO,
                                width = videoMedia.width,
                                height = videoMedia.height,
                                mediaQualityType = videoMedia.mediaQualityType,
                            )
                        )
                    )
                    updateSourceMedia(it, sourceMedia)
                    logger.info("Updated source media for postId: ${it.postId}")
                } else {
                    logger.error("Input file path not found for post: ${it.postId}")
                }
            } else {
                updateSourceMedia(it, media)
                logger.info("Updated source media for postId: ${it.postId}")
            }
        }
    }

    // Only one time thing to process old data
    private fun updateSourceMedia(post: Post, sourceMedia: MediaDetailsV2): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(sourceMedia = sourceMedia.convertToString()))
            val result = PostUpdate(listOf(PostUpdateType.MEDIA), post, updatedPost)
            updatePostProcessing(result, ProcessingType.REFRESH)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            PostUpdate(listOf(PostUpdateType.MEDIA), post, null)
        }
    }

    fun updateMedia(post: Post, media: MediaDetailsV2, processingType: ProcessingType = ProcessingType.NO_PROCESSING): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(media = media.convertToString()))
            val result = PostUpdate(listOf(PostUpdateType.MEDIA), post, updatedPost)
            updatePostProcessing(result, processingType)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            PostUpdate(listOf(PostUpdateType.MEDIA), post, null)
        }
    }

    fun updateLabels(post: Post, labels: Set<String>, processingType: ProcessingType = ProcessingType.REFRESH): PostUpdate {
        return try {
            val labelsStr = AllLabelsResponse(labels).convertToString()
            val updatedPost = postRepository.save(post.copy(labels = labelsStr))
            val result = PostUpdate(listOf(PostUpdateType.LABELS), post, updatedPost)
            updatePostProcessing(result, processingType)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the for postId: ${post.postId}")
            PostUpdate(listOf(PostUpdateType.LABELS), post, null)
        }
    }

    fun updateTitle(post: Post, title: String): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(title = title))
            val result = PostUpdate(listOf(PostUpdateType.TITLE), post, updatedPost)
            updatePostProcessing(result, ProcessingType.REFRESH)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the post for postId: ${post.postId}")
            PostUpdate(listOf(PostUpdateType.TITLE), post, null)
        }
    }

    fun updateDescription(post: Post, description: String): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(description = description))
            val result = PostUpdate(listOf(PostUpdateType.DESCRIPTION), post, updatedPost)
            updatePostProcessing(result, ProcessingType.REFRESH)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the post for postId: ${post.postId}")
            PostUpdate(listOf(PostUpdateType.DESCRIPTION), post, null)
        }
    }

    fun updateTags(post: Post, tags: Set<String>): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(tags = AllHashTags(tags).convertToString()))
            val result = PostUpdate(listOf(PostUpdateType.TAGS), post, updatedPost)
            updatePostProcessing(result, ProcessingType.DELETE_AND_REFRESH)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the post for postId: ${post.postId}")
            PostUpdate(listOf(PostUpdateType.TAGS), post, null)
        }
    }

    fun updateCategories(post: Post, categories: Set<CategoryV2>): PostUpdate {
        return try {
            val updatedPost = postRepository.save(post.copy(categories = CommonUtils.convertToStringBlob(
                AllCategoryV2Response(
                categories.map { it.toCategoryV2Response() }
            ))))
            val result = PostUpdate(listOf(PostUpdateType.CATEGORIES), post, updatedPost)
            updatePostProcessing(result, ProcessingType.DELETE_AND_REFRESH)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating the post for postId: ${post.postId}")
            PostUpdate(listOf(PostUpdateType.CATEGORIES), post, null)
        }
    }

    fun update(request: UpdatePostRequest): SavedPostResponse? {
        val loggedInUserId = securityProvider.validateRequest().getUserIdToUse()
        val post = getPost(request.postId) ?: error("No post found for postId: ${request.postId}")
        if (post.userId != loggedInUserId) {
            error("User $loggedInUserId is not authorized to update post: ${request.postId}. User can only update their own post.")
        }
        val oldPost = post
        var tempPost = post
        val updateTypes = mutableListOf<PostUpdateType>()

        val titleIsUpdated = request.title != null && post.title != request.title
        val descriptionIsUpdated = request.description != null && post.description != request.description
        val isMediaUpdated = request.mediaDetails != null && request.mediaDetails.media.isNotEmpty()

        var tagsAreUpdated = false
        if (request.tags != null) {
            val isTagsSizeSame = post.getHashTags().tags.size == request.tags.size
            val areTagsSame = post.getHashTags().tags.containsAll(request.tags)
            tagsAreUpdated = !isTagsSizeSame || !areTagsSame
            if (tagsAreUpdated) {
                tempPost = tempPost.copy(tags = AllHashTags(request.tags).convertToString())
                updateTypes.add(PostUpdateType.TAGS)
            }
        }

        var categoriesAreUpdated = false
        if (request.categories != null) {
            val postCategories = post.getCategories().categories.map { it.id }.toSet()
            val requestCategories = request.categories.map { it }.toSet()
            val isCategoriesSizeSame = postCategories.size == requestCategories.size
            val areCategoriesSame = postCategories.containsAll(requestCategories)
            categoriesAreUpdated = !isCategoriesSizeSame || !areCategoriesSame
            if (categoriesAreUpdated) {
                tempPost = tempPost.copy(categories = CommonUtils.convertToStringBlob(AllCategoryV2Response(
                    request.categories.map { it.toCategoryV2Response() }
                )))
                updateTypes.add(PostUpdateType.CATEGORIES)
            }
        }

        if (titleIsUpdated) {
            tempPost = tempPost.copy(title = request.title)
            updateTypes.add(PostUpdateType.TITLE)
        }

        if (descriptionIsUpdated) {
            tempPost = tempPost.copy(description = request.description)
            updateTypes.add(PostUpdateType.DESCRIPTION)
        }

        if (isMediaUpdated) {
            tempPost = tempPost.copy(media = request.mediaDetails?.convertToString())
            updateTypes.add(PostUpdateType.MEDIA)
        }

        val processingType: ProcessingType = if (categoriesAreUpdated || tagsAreUpdated) {
            ProcessingType.DELETE_AND_REFRESH
        } else if (titleIsUpdated || descriptionIsUpdated || isMediaUpdated) {
            ProcessingType.REFRESH
        } else {
            ProcessingType.NO_PROCESSING
        }

        val updatedPost = postRepository.save(tempPost)
        val result = PostUpdate(updateTypes, oldPost, updatedPost)
        updatePostProcessing(result, processingType)
        return updatedPost.toSavedPostResponse()
    }

    fun updatePostProcessing(postUpdate: PostUpdate, processingType: ProcessingType) {
        val updatedPost = postUpdate.newPost
        logger.info("Post updated. old: ${postUpdate.oldPost} new: ${updatedPost} and processingType: $processingType")
        if (updatedPost == null) {
            logger.error("Post update does not have updated post data. So skipping the update expansion.")
            return
        }

        if (processingType == ProcessingType.NO_PROCESSING) {
            logger.info("Post update does not require any post processing. So skipping the update expansion.")
            return
        }
        GlobalScope.launch {
            logger.info("Start: Update post and other dependent information for postId: ${updatedPost.postId}")

            bookmarkedPostsByUserProvider.updatePostExpandedData(postUpdate, processingType)
            likedPostsByUserProvider.updatePostExpandedData(postUpdate, processingType)
            postsByCategoryProvider.updatePostExpandedData(postUpdate, processingType)
            postsByPostTypeProvider.updatePostExpandedData(postUpdate, processingType)
            postsByFollowingProvider.updatePostExpandedData(postUpdate, processingType)
            postsByHashTagProvider.updatePostExpandedData(postUpdate, processingType)
            postsByUserProvider.updatePostExpandedData(postUpdate, processingType)
            postsByZipcodeProvider.updatePostExpandedData(postUpdate, processingType)
            nearbyPostsByZipcodeProvider.updatePostExpandedData(postUpdate, processingType)
            nearbyVideoPostsByZipcodeProvider.updatePostExpandedData(postUpdate, processingType)
            searchProvider.doSearchProcessingForPost(updatedPost)

            logger.info("End: Update post and other dependent information for postId: ${updatedPost.postId}")
        }
    }

    fun report(request: PostReportRequest): PostReportResponse? {
        takeReportAction(request)
        return  PostReportResponse(
            reportedByUserId = request.reportedByUserId,
            postId = request.postId,
            reason = request.reason,
            action = request.action,
            actionDetails = "We have registered your complaint and we will take an action within 24 hours. Thank you for helping us make Unbox a better place for everyone.",
        )
    }

    fun takeReportAction(request: PostReportRequest) {
        GlobalScope.launch {
            postReportByUserRepository.save(
                PostReportV2ByUser(
                    postId = request.postId,
                    reportedByUserId = request.reportedByUserId,
                    reason = request.reason,
                    action = request.action,
                )
            )

            val post = getPost(request.postId) ?: error("Post not found for postId: ${request.postId}")
            val user = userV2Provider.getUser(request.reportedByUserId) ?: error("User not found for reportedByUserId: ${request.reportedByUserId}")

            automationProvider.sendSlackMessageForPostReport(request, user, post)
        }
    }

    fun getAllReport(userId: String): AllPostReportResponse? {
        val reports = postReportByUserRepository.findAllByReportedByUserId(userId)
        return AllPostReportResponse(
            reports = reports.map {
                PostReportResponse(
                    reportedByUserId = it.reportedByUserId,
                    postId = it.postId,
                    reason = it.reason,
                    action = it.action,
                    actionDetails = "Reported",
                )
            }
        )
    }

}
