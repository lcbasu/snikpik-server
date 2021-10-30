package com.server.ud.provider.post

import com.server.ud.entities.post.getCategories
import com.server.ud.entities.post.getHashTags
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
            val hashTagsFeedFuture = async {
                post.getHashTags()
                    .map { async { postsByHashTagProvider.save(post, it) } }
                    .map { it.await() }
            }

            postsByUserFuture.await()
            postsByZipcodeFuture.await()
            followersFeedFuture.await()
            categoriesFeedFuture.await()
            hashTagsFeedFuture.await()

            logger.info("Post processing completed for postId: $postId")
        }
    }

}
