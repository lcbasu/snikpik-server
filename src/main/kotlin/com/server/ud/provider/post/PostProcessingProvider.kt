package com.server.ud.provider.post

import com.server.ud.entities.post.getCategories
import com.server.ud.entities.post.getHashTags
import com.server.ud.enums.CategoryV2
import com.server.ud.provider.location.ESLocationProvider
import com.server.ud.provider.location.LocationProcessingProvider
import com.server.ud.provider.social.FollowerProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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
    private lateinit var followerProvider: FollowerProvider

    @Autowired
    private lateinit var postsByFollowingProvider: PostsByFollowingProvider

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    @Autowired
    private lateinit var postsByHashTagProvider: PostsByHashTagProvider

    @Autowired
    private lateinit var esPostProvider: ESPostProvider

    @Autowired
    private lateinit var esLocationProvider: ESLocationProvider

    @Autowired
    private lateinit var locationProcessingProvider: LocationProcessingProvider

    fun postProcessPost(postId: String) {
        // Update
        // Post By User
        // Post By Zipcode (Nearby Feed)
        // Follower Feed
        // Category Feed
        // Tags Feed
        runBlocking {
            logger.info("Do post processing for postId: $postId")
            val post = postProvider.getPost(postId) ?: error("No post found for $postId while doing post processing.")
            val esLocation = post.locationId?.let { esLocationProvider.getLocation(it) }
            if (esLocation == null) {
                // Location not processed.
                // Process the location
                locationProcessingProvider.processLocation(post.locationId!!)
            }
            val postsByUserFuture = async {
                postsByUserProvider.save(post)
            }
            val postsByZipcodeFuture = async {
                postsByZipcodeProvider.save(post)
            }
            val followersFeedFuture = async {
                followerProvider.getFollowers(post.userId)
                ?.map { async { it.followerUserId?.let { postsByFollowingProvider.save(post, it) } } }
                ?.map { it.await() }
            }
            val categoriesFeedFuture = async {
                post.getCategories()
                    .map { async { postsByCategoryProvider.save(post, it) } }
                    .map { it.await() }
            }
            val allCategoryFeedFuture = async { postsByCategoryProvider.save(post, CategoryV2.ALL) }
            val hashTagsFeedFuture = async {
                post.getHashTags()
                    .map { async { postsByHashTagProvider.save(post, it) } }
                    .map { it.await() }
            }

            val savePostToESFuture = async {
                esPostProvider.save(post)
            }

            postsByUserFuture.await()
            postsByZipcodeFuture.await()
            followersFeedFuture.await()
            categoriesFeedFuture.await()
            allCategoryFeedFuture.await()
            hashTagsFeedFuture.await()
            savePostToESFuture.await()

            logger.info("Post processing completed for postId: $postId")
        }
    }

}
