package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByHashTagRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByHashTag
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsByHashTagProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByHashTagRepository: PostsByHashTagRepository

    fun save(post: Post, hashTagId: String): PostsByHashTag? {
        try {
            val postsByHashTag = PostsByHashTag(
                hashTagId = hashTagId,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                title = post.title,
                description = post.description,
                media = post.media,
                sourceMedia = post.sourceMedia,
                tags = post.tags,
                categories = post.categories,
                locality = post.locality,
                subLocality = post.subLocality,
                route = post.route,
                city = post.city,
                state = post.state,
                country = post.country,
                countryCode = post.countryCode,
                completeAddress = post.completeAddress,
            )
            return postsByHashTagRepository.save(postsByHashTag)
        } catch (e: Exception) {
            logger.error("Saving PostsByHashTag filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            postsByHashTagRepository.deleteAll(postsByHashTagRepository.findAllByPostId(postId))
        }
    }

    fun updatePostExpandedData(post: Post) {
        GlobalScope.launch {

        }
    }
}
