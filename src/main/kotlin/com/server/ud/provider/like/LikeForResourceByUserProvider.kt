package com.server.ud.provider.like

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.like.LikeForResourceByUserRepository
import com.server.ud.entities.like.LikeForResourceByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LikeForResourceByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var likeForResourceByUserRepository: LikeForResourceByUserRepository

    fun getLikeForResourceByUser(resourceId: String, userId: String): LikeForResourceByUser? =
    try {
        val resourceLikes = likeForResourceByUserRepository.findAllByResourceIdAndUserId(resourceId, userId)
        if (resourceLikes.size > 1) {
            error("More than one likes has same resourceId: $resourceId by the userId: $userId")
        }
        resourceLikes.firstOrNull()
    } catch (e: Exception) {
        logger.error("Getting LikeForResourceByUser for $resourceId & userId: $userId failed.")
        e.printStackTrace()
        null
    }

    fun save(resourceId: String, userId: String, liked: Boolean) : LikeForResourceByUser? {
        return try {
            val like = LikeForResourceByUser(
                resourceId = resourceId,
                userId = userId,
                liked = liked,
            )
            val result = likeForResourceByUserRepository.save(like)
            saveLikeForResourceByUserToFirestore(result)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setLike(resourceId: String, userId: String, value: Boolean) {
        // Using save instead of update as they both would eventually do the same thing for
        // single row documents
        save(resourceId, userId, value)
    }

    private fun saveLikeForResourceByUserToFirestore (likeForResourceByUser: LikeForResourceByUser) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("users")
                .document(likeForResourceByUser.userId)
                .collection("like_for_resource_by_user")
                .document(likeForResourceByUser.resourceId)
                .set(likeForResourceByUser)
        }
    }

    fun saveAllToFirestore() {
        likeForResourceByUserRepository.findAll().forEach {
            saveLikeForResourceByUserToFirestore(it!!)
        }
    }

}
