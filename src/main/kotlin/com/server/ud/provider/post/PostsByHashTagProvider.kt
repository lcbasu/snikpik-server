package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByHashTagRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByHashTag
import com.server.ud.model.HashTagData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsByHashTagProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByHashTagRepository: PostsByHashTagRepository

    fun save(post: Post, hashTagData: HashTagData): PostsByHashTag? {
        try {
            val postsByHashTag = PostsByHashTag(
                hashTagId = hashTagData.tagId,
                forDate = DateUtils.getInstantDate(post.createdAt),
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                hashTagDisplayName = hashTagData.displayName,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                title = post.title,
                description = post.description,
                media = post.media,
                tags = post.tags,
                categories = post.categories,
            )
            return postsByHashTagRepository.save(postsByHashTag)
        } catch (e: Exception) {
            logger.error("Saving PostsByHashTag filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

}
