package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByZipcodeRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByZipcode
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
            postsByZipcodeRepository.deleteAll(postsByZipcodeRepository.findAllByPostId(postId))
        }
    }

}
