package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByFollowingRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByFollowing
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
                tags = post.tags,
                categories = post.categories,
                locationId = post.locationId,
                zipcode = post.zipcode!!,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
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
}
