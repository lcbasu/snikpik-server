package com.server.ud.provider.post

import com.server.ud.dao.es.post.ESPostAutoSuggestRepository
import com.server.ud.entities.es.post.ESPostAutoSuggest
import com.server.ud.entities.post.Post
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ESPostAutoSuggestProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var esPostAutoSuggestRepository: ESPostAutoSuggestRepository

    fun save(post: Post) : ESPostAutoSuggest? {
        try {
            val savedAutoSuggest = esPostAutoSuggestRepository.save(ESPostAutoSuggest(
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

            logger.info("Saved post auto suggest to elastic search postId: ${savedAutoSuggest.postId}")
            return savedAutoSuggest
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Saving post auto suggest to elastic search failed for postId: ${post.postId}")
            return null
        }
    }

}
