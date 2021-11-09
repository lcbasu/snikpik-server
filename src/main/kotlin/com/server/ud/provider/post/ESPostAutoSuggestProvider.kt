package com.server.ud.provider.post

import com.server.ud.dao.es.post.ESPostAutoSuggestRepository
import com.server.ud.entities.es.post.ESPostAutoSuggest
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getCategories
import com.server.ud.entities.post.getHashTags
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
            val tagsIds = post.getHashTags().tags.map { it.tagId }.toSet()
            val tagsNames = post.getHashTags().tags.map { it.displayName }.toSet()
            val categories = post.getCategories().map { it.displayName }.toSet()
            val categoriesGroups = post.getCategories().map { it.categoryGroup.displayName }.toSet()
            val savedAutoSuggest = esPostAutoSuggestRepository.save(ESPostAutoSuggest(
                postId = post.postId,
                suggestionText = (setOf(
                    post.media,
                    post.title,
                    post.description,
                    post.locationName,
                    post.userHandle,
                    post.userMobile,
                    post.userName,
                    post.zipcode,
//                    "Embassy Golf Links Business Park",
//                    "Golf Links Business Park",
//                    "Links Business Park",
//                    "Business Park",
//                    "Park",
                ) + tagsIds + tagsNames + categories + categoriesGroups).filterNotNull().toSet()
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
