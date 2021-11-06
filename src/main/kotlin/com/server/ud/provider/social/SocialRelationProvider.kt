package com.server.ud.provider.social

import com.server.ud.dao.social.SocialRelationRepository
import com.server.ud.entities.social.SocialRelation
import com.server.ud.service.post.ProcessPostSchedulerService
import com.server.ud.service.social.ProcessSocialRelationSchedulerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SocialRelationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var socialRelationRepository: SocialRelationRepository

    @Autowired
    private lateinit var processSocialRelationSchedulerService: ProcessSocialRelationSchedulerService

    fun getSocialRelation(fromUserId: String, toUserId: String): SocialRelation? =
    try {
        val resourceLikes = socialRelationRepository.getAllByUserAndOtherUser(fromUserId, toUserId)
        if (resourceLikes.size > 1) {
            error("More than one social relation present for fromUserId: $fromUserId & toUserId: $toUserId")
        }
        resourceLikes.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting SocialRelation for fromUserId: $fromUserId & toUserId: $toUserId failed.")
        e.printStackTrace()
        null
    }

    fun save(fromUserId: String, toUserId: String, following: Boolean) : SocialRelation? {
        try {
            val relation = SocialRelation(
                fromUserId = fromUserId,
                toUserId = toUserId,
                following = following,
            )
            val savedRelation = socialRelationRepository.save(relation)
            processSocialRelationSchedulerService.createSocialRelationProcessingJob(savedRelation)
            return savedRelation
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
