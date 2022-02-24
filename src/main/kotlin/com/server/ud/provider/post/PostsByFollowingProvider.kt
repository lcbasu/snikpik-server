package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByFollowingRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostUpdate
import com.server.ud.entities.post.PostsByFollowing
import com.server.ud.enums.ProcessingType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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


    fun update(postsByFollowing: PostsByFollowing, updatedPost: Post): PostsByFollowing? {
        try {
            val saved = postsByFollowingRepository.save(postsByFollowing.copy(
                followingUserId = updatedPost.userId,
                createdAt = updatedPost.createdAt,
                postId = updatedPost.postId,
                postType = updatedPost.postType,
                title = updatedPost.title,
                description = updatedPost.description,
                media = updatedPost.media,
                sourceMedia = updatedPost.sourceMedia,
                tags = updatedPost.tags,
                categories = updatedPost.categories,
                locationId = updatedPost.locationId,
                zipcode = updatedPost.zipcode!!,
                locationName = updatedPost.locationName,
                locationLat = updatedPost.locationLat,
                locationLng = updatedPost.locationLng,
                locality = updatedPost.locality,
                subLocality = updatedPost.subLocality,
                route = updatedPost.route,
                city = updatedPost.city,
                state = updatedPost.state,
                country = updatedPost.country,
                countryCode = updatedPost.countryCode,
                completeAddress = updatedPost.completeAddress,
            ))
            logger.info("Updated PostsByFollowing for postId:${saved.postId}, followingUserId: ${saved.followingUserId} and followingUserId: ${saved.followingUserId}.")
            return saved
        } catch (e: Exception) {
            logger.error("Updating PostsByFollowing failed for ${postsByFollowing.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getAllByPostId(postId: String) = postsByFollowingRepository.findAllByPostId_V2(postId)

    fun deletePostExpandedData(postId: String) {
        val maxDeleteSize = 5
        val posts = getAllByPostId(postId)
        logger.info("Deleting post $postId from PostsByFollowing. Total ${posts.size} PostsByFollowing entries needs to be deleted.")
        postsByFollowingRepository.deleteAll(posts)
        logger.info("Deleted all entries for PostsByFollowing for post $postId from PostsByFollowing.")
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            val updatedPost = postUpdate.newPost!!
            val all = getAllByPostId(updatedPost.postId)
            all.chunked(5).map {
                async { it.map { update(it, updatedPost) } }
            }.map {
                it.await()
            }
        }
    }
}
