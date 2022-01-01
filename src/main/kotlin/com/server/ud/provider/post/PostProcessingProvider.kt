package com.server.ud.provider.post

import com.server.common.provider.MediaHandlerProvider
import com.server.ud.dao.post.*
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.entities.location.Location
import com.server.ud.entities.post.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.provider.deferred.DeferredProcessingProvider
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.location.ESLocationProvider
import com.server.ud.provider.location.LocationProcessingProvider
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.provider.search.SearchProvider
import com.server.ud.provider.social.FollowersByUserProvider
import com.server.ud.provider.user_activity.UserActivitiesProvider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postProvider: PostProvider

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

//    @Autowired
//    private lateinit var esPostProvider: ESPostProvider

//    @Autowired
//    private lateinit var esPostAutoSuggestProvider: ESPostAutoSuggestProvider

    @Autowired
    private lateinit var esLocationProvider: ESLocationProvider

    @Autowired
    private lateinit var locationProcessingProvider: LocationProcessingProvider

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var nearbyPostsByZipcodeProvider: NearbyPostsByZipcodeProvider

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeProvider: NearbyVideoPostsByZipcodeProvider

    @Autowired
    private lateinit var searchProvider: SearchProvider

    @Autowired
    private lateinit var userActivityProvider: UserActivitiesProvider

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserRepository: BookmarkedPostsByUserRepository

    @Autowired
    private lateinit var likedPostsByUserRepository: LikedPostsByUserRepository

    @Autowired
    private lateinit var nearbyPostsByZipcodeRepository: NearbyPostsByZipcodeRepository

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeRepository: NearbyVideoPostsByZipcodeRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var postsByCategoryRepository: PostsByCategoryRepository

    @Autowired
    private lateinit var postsByFollowingRepository: PostsByFollowingRepository

    @Autowired
    private lateinit var postsByHashTagRepository: PostsByHashTagRepository

    @Autowired
    private lateinit var postsByUserRepository: PostsByUserRepository

    @Autowired
    private lateinit var postsByZipcodeRepository: PostsByZipcodeRepository

    @Autowired
    private lateinit var postsCountByUserRepository: PostsCountByUserRepository

    fun postProcessPost(postId: String) {
        // Update
        // Post By User
        // Post By Zipcode (Nearby Feed)
        // Follower Feed
        // Category Feed
        // Tags Feed
        GlobalScope.launch {
            logger.info("Start: post processing for postId: $postId")
            val post = postProvider.getPost(postId) ?: error("No post found for $postId while doing post processing.")

            val userActivityFuture = async {
                userActivityProvider.savePostCreationActivity(post)
            }

            val labels = mediaHandlerProvider.getLabelsForMedia(post.getMediaDetails())

            val updatedPost = if (labels.isNotEmpty()) {
                postProvider.updateLabels(post, labels)
                postProvider.getPost(postId) ?: error("No post found for $postId while doing post processing.")
            } else {
                post
            }
            val esLocation = updatedPost.locationId?.let { esLocationProvider.getLocation(it) }
            if (esLocation == null) {
                // Location not processed.
                // Process the location
                locationProcessingProvider.processLocation(updatedPost.locationId!!)
            }

            val postsByUserFuture = async {
                postsByUserProvider.save(updatedPost)
            }

            val postsCountByUserFuture = async {
                postsCountByUserProvider.increasePostCount(updatedPost.userId)
            }

            val postsByZipcodeFuture = async {
                postsByZipcodeProvider.save(updatedPost)
            }

            val saveForNearbyPosts = async {
                post.zipcode?.let {
                    val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(it)
                    nearbyPostsByZipcodeProvider.save(updatedPost, nearbyZipcodes)
                    nearbyVideoPostsByZipcodeProvider.save(updatedPost, nearbyZipcodes)
                }
            }

            val categoriesFeedFuture = async {
                updatedPost.getCategories().categories
                    .map { async { postsByCategoryProvider.save(post, it.id) } }
                    .map { it.await() }
            }

            val allCategoryFeedFuture = async {
                postsByCategoryProvider.save(updatedPost, CategoryV2.ALL)
            }

            val hashTagsFeedFuture = async {
                updatedPost.getHashTags().tags
                    .map { async { postsByHashTagProvider.save(post, it) } }
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
            postsCountByUserFuture.await()
            postsByZipcodeFuture.await()
            saveForNearbyPosts.await()
            categoriesFeedFuture.await()
            allCategoryFeedFuture.await()
            hashTagsFeedFuture.await()
//            savePostToESFuture.await()
//            savePostAutoSuggestToESFuture.await()
            algoliaIndexingFuture.await()

            // Schedule Heavy job to be done in isolation
            udJobProvider.scheduleProcessingForPostForFollowers(postId)

            logger.info("Done: post processing for postId: $postId")
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
            val post = postProvider.getPost(postId) ?: error("No post found for $postId while doing post processing.")
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
                        limit = 10,
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

    fun deletePost(postId: String, userId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete post and other dependent information for postId: $postId")

            bookmarkedPostsByUserRepository.deleteAll(bookmarkedPostsByUserRepository.findAllByPostId(postId))
            likedPostsByUserRepository.deleteAll(likedPostsByUserRepository.findAllByPostId(postId))
            nearbyPostsByZipcodeRepository.deleteAll(nearbyPostsByZipcodeRepository.findAllByPostId(postId))
            nearbyVideoPostsByZipcodeRepository.deleteAll(nearbyVideoPostsByZipcodeRepository.findAllByPostId(postId))
            postsByCategoryRepository.deleteAll(postsByCategoryRepository.findAllByPostId(postId))
            postsByFollowingRepository.deleteAll(postsByFollowingRepository.findAllByPostId(postId))
            postsByHashTagRepository.deleteAll(postsByHashTagRepository.findAllByPostId(postId))
            postsByUserRepository.deleteAll(postsByUserRepository.findAllByPostId(postId))
            postsByZipcodeRepository.deleteAll(postsByZipcodeRepository.findAllByPostId(postId))

            // Only one count has to be decreases as the one post is created by
            // only one user and adds only one count
            postsCountByUserRepository.decrementPostCount(userId)

            postRepository.deleteAll(postRepository.findAllByPostId(postId))

            logger.info("End: Delete post and other dependent information for postId: $postId")
        }
    }

}
