package com.server.ud.provider.post

import com.server.ud.dao.post.PostsByZipcodeRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostUpdate
import com.server.ud.entities.post.PostsByZipcode
import com.server.ud.enums.ProcessingType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByZipcodeRepository: PostsByZipcodeRepository

    fun save(post: Post): PostsByZipcode? {
        try {
            if (post.zipcode == null) {
                logger.error("Post does not have location zipcode. Hence unable to save into PostsByZipcode for postId: ${post.postId}.")
                return null
            }
            val postsByZipcode = PostsByZipcode(
                zipcode = post.zipcode!!,
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
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
            return postsByZipcodeRepository.save(postsByZipcode)
        } catch (e: Exception) {
            logger.error("Saving PostsByZipcode filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            val all = postsByZipcodeRepository.findAllByPostId(postId)
            all.chunked(5).forEach {
                postsByZipcodeRepository.deleteAll(it)
            }
        }
    }

    fun processPostExpandedData(post: Post) {
        GlobalScope.launch {
            save(post)
        }
    }

    fun updatePostExpandedData(postUpdate: PostUpdate, processingType: ProcessingType) {
        GlobalScope.launch {
            when (processingType) {
                ProcessingType.NO_PROCESSING -> logger.error("This should not happen. Updating the zipcode without processing should never happen.")
                ProcessingType.DELETE_AND_REFRESH -> {
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
