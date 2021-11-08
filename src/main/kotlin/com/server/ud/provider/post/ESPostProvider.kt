package com.server.ud.provider.post

import com.server.ud.dao.es.post.ESPostAutoSuggestRepository
import com.server.ud.dao.es.post.ESPostRepository
import com.server.ud.entities.es.post.ESPost
import com.server.ud.entities.es.post.ESPostAutoSuggest
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

    @Autowired
    private lateinit var esPostAutoSuggestRepository: ESPostAutoSuggestRepository

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
                tags = post.getHashTags().tags,
//                categories = post.getCategories(),
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                locationName = post.locationName,
                geoPoint = post.getGeoPointData(),
                userHandle = post.userHandle,
                userMobile = post.userMobile,
                userName = post.userName,
                userCountryCode = post.userCountryCode,
                userProfile = post.userProfile
            )
            val savedESPost = esPostRepository.save(esPost)

            esPostAutoSuggestRepository.save(ESPostAutoSuggest(
                postId = post.postId,
                suggestionText = setOf(
                    post.media,
                    post.description,
                    post.locationName,
                    post.userHandle,
                    post.userMobile,
                    post.userName,
//                    "Embassy Golf Links Business Park",
//                    "Golf Links Business Park",
//                    "Links Business Park",
//                    "Business Park",
//                    "Park",
                ).filterNotNull().toSet()
            ))

            logger.info("Saved post to elastic search postId: ${savedESPost.postId}")
            return savedESPost
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Saving post to elastic search failed for postId: ${post.postId}")
            return null
        }
    }

}
