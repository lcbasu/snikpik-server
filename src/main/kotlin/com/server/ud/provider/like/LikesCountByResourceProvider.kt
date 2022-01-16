package com.server.ud.provider.like

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.like.LikesCountByResourceRepository
import com.server.ud.entities.like.LikesCountByResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikesCountByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likesCountByResourceRepository: LikesCountByResourceRepository

    fun getLikesCountByResource(resourceId: String): LikesCountByResource? =
    try {
        val resourceLikes = likesCountByResourceRepository.findAllByResourceId(resourceId)
        if (resourceLikes.size > 1) {
            error("More than one likes has same resourceId: $resourceId")
        }
        resourceLikes.getOrElse(0) {
            val likesCountByResource = LikesCountByResource()
            likesCountByResource.likesCount = 0
            likesCountByResource.resourceId = resourceId
            likesCountByResource
        }
    } catch (e: Exception) {
        logger.error("Getting LikesCountByResource for $resourceId failed.")
        e.printStackTrace()
        null
    }

    fun increaseLike(resourceId: String) {
        likesCountByResourceRepository.incrementLikes(resourceId)
        logger.warn("Increased like for resourceId: $resourceId")
        saveLikesCountByResourceToFirestore(getLikesCountByResource(resourceId))
    }

    fun decreaseLike(resourceId: String) {
        val existing = getLikesCountByResource(resourceId)
        if (existing?.likesCount != null && existing.likesCount!! > 0) {
            likesCountByResourceRepository.decrementLikes(resourceId)
            logger.warn("Decreased like for resourceId: $resourceId")
        } else {
            logger.warn("The likes count is already zero. So skipping decreasing it further for resourceId: $resourceId")
        }
        saveLikesCountByResourceToFirestore(getLikesCountByResource(resourceId))
    }

    fun deletePost(postId: String) {
        TODO("Add steps to delete post and related information")
    }

    private fun saveLikesCountByResourceToFirestore (likesCountByResource: LikesCountByResource?) {
        GlobalScope.launch {
            if (likesCountByResource?.resourceId == null) {
                logger.error("No resource id found in likesCountByResource. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("likes_count_by_resource")
                .document(likesCountByResource.resourceId!!)
                .set(likesCountByResource)
        }
    }

    fun saveAllToFirestore() {
        likesCountByResourceRepository.findAll().forEach {
            saveLikesCountByResourceToFirestore(it)
        }
    }

}
