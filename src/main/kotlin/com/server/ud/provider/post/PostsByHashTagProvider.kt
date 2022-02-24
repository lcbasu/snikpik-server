package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByHashTagRepository
import com.server.ud.entities.post.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.ProcessingType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        val all = postsByHashTagRepository.findAllByPostId_V2(postId)
        postsByHashTagRepository.deleteAll(all)
    }

    fun processPostExpandedData(post: Post) {
        runBlocking {
            post.getHashTags().tags
                .map { async { save(post, it) } }
                .map { it.await() }
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating the hash-tags without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
                    // Do not run delete and update in parallel
                    // Delete old data
                    deletePostExpandedData(postUpdate.oldPost.postId)
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
                ProcessingType.REFRESH -> {
                    // Index the new data
                    processPostExpandedData(postUpdate.newPost!!)
                }
            }
        }
    }
}
