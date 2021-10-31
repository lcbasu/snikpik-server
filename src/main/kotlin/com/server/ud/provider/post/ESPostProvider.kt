package com.server.ud.provider.post

import com.server.ud.dao.es.post.ESPostRepository
import com.server.ud.entities.es.post.ESPost
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getGeoPointData
import com.server.ud.entities.post.getHashTags
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ESPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var esPostRepository: ESPostRepository

    fun save(post: Post) : ESPost? {
        try {
            val esPost = ESPost(
                postId = post.postId,
                userId = post.userId,
                createdAt = post.createdAt,
                postType = post.postType,
                title = post.title,
                description = post.description,
                media = post.media,
                tags = post.getHashTags(),
//                categories = post.getCategories(),
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                locationName = post.locationName,
                geoPoint = post.getGeoPointData()
            )
            val savedESPost = esPostRepository.save(esPost)
            logger.info("Saved post to elastic search postId: ${savedESPost.postId}")
            return savedESPost
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Saving post to elastic search failed for postId: ${post.postId}")
            return null
        }
    }

}
