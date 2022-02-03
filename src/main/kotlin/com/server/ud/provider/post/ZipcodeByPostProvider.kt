package com.server.ud.provider.post

import com.server.ud.dao.post.ZipcodeByPostRepository
import com.server.ud.entities.location.NearbyZipcodesByZipcode
import com.server.ud.entities.post.ZipcodeByPost
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ZipcodeByPostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var zipcodeByPostRepository: ZipcodeByPostRepository

    fun getZipcodesByPost(postId: String): List<ZipcodeByPost> =
        try {
            zipcodeByPostRepository.findAllByPostId(postId)
        } catch (e: Exception) {
            logger.error("Getting ZipcodeByPost for $postId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun save(postId: String, nearbyZipcodes: List<NearbyZipcodesByZipcode>): List<ZipcodeByPost> {
        return try {
            val zipcodeByPosts = nearbyZipcodes.map {
                ZipcodeByPost(
                    postId = postId,
                    zipcode = it.nearbyZipcode,
                )
            }
            zipcodeByPostRepository.saveAll(zipcodeByPosts)
        } catch (e: Exception) {
            logger.error("Saving ZipcodeByPost filed for postId: ${postId} and zipcodes: ${nearbyZipcodes.toString()}.")
            e.printStackTrace()
            emptyList()
        }
    }

    fun deletePostExpandedData(postId: String) {
        GlobalScope.launch {
            zipcodeByPostRepository.deleteAll(zipcodeByPostRepository.findAllByPostId(postId))
        }
    }

}
