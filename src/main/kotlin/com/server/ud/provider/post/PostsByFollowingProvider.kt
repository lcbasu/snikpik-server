package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByFollowingRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByFollowing
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsByFollowingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByFollowingRepository: PostsByFollowingRepository

    fun save(post: Post, followerId: String): PostsByFollowing? {
        try {
            val postsByFollowing = PostsByFollowing(
                userId = followerId,
                followingUserId = post.userId,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
                locationId = post.locationId,
                zipcode = post.zipcode!!,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                locality = post.locality,
                subLocality = post.subLocality,
                route = post.route,
                city = post.city,
                state = post.state,
                country = post.country,
                countryCode = post.countryCode,
                completeAddress = post.completeAddress,
            )
            val saved = postsByFollowingRepository.save(postsByFollowing)
            logger.info("Saved PostsByFollowing for postId:${saved.postId}, followerId: $followerId and followingUserId: ${saved.followingUserId}.")
            return saved
        } catch (e: Exception) {
            logger.error("Saving PostsByFollowing failed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            val maxDeleteSize = 5
            val posts = postsByFollowingRepository.findAllByPostId(postId)
            logger.info("Deleting post $postId from NearbyPostsByZipcode. Total ${posts.size} PostsByFollowing entries needs to be deleted.")
            posts.chunked(maxDeleteSize).map {
                postsByFollowingRepository.deleteAll(it)
                logger.info("Deleted maxDeleteSize: ${it.size} PostsByFollowing entries.")
            }
            logger.info("Deleted all entries for PostsByFollowing for post $postId from PostsByFollowing.")
        }
    }

    fun updatePostExpandedData(post: Post) {
        GlobalScope.launch {

        }
    }
}
