package com.server.sp.provider.moment

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.model.getSanitizedMediaDetails
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.sp.dao.moment.SpMomentRepository
import com.server.sp.dto.*
import com.server.sp.entities.moment.SpMoment
import com.server.sp.entities.user.SpMomentsCountByUser
import com.server.sp.model.convertToString
import com.server.sp.provider.job.SpJobProvider
import com.server.sp.provider.user.SpUserProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpMomentProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var spJobProvider: SpJobProvider

    @Autowired
    private lateinit var spMomentRepository: SpMomentRepository

    @Autowired
    private lateinit var spUserProvider: SpUserProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun getSpMoment(momentId: String): SpMoment? =
        try {
            val moments = spMomentRepository.findAllByMomentId(momentId)
            if (moments.size > 1) {
                error("More than one moment has same momentId: $momentId")
            }
            moments.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting SpMoment for $momentId failed.")
            e.printStackTrace()
            null
        }

    fun saveMoment(request: SaveSpMomentRequest): SavedSpMomentResponse? {
        try {
            val requestContext = securityProvider.validateRequest()
            return saveMoment(requestContext.getUserIdToUse(), request)?.toSavedSpMomentResponse()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveMoment(userId: String, request: SaveSpMomentRequest): SpMoment? {
        try {
            val user = spUserProvider.getSpUser(userId) ?: error("Missing user for userId: $userId")

            val moment = SpMoment(
                momentId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.MNT.name),
                userId = user.userId,
                momentType = request.momentType,
                momentMediaType = request.momentMediaType,
                challengeId = request.challengeId,
                momentTaggedUserDetails = request.momentTaggedUserDetails.convertToString(),
                title = request.title,
                description = request.description,
                mediaDetails = request.mediaDetails?.getSanitizedMediaDetails()?.convertToString(),
                sourceMedia = request.mediaDetails?.getSanitizedMediaDetails()?.convertToString(),
            )
            return saveMoment(moment)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveMoment(moment: SpMoment): SpMoment? {
        try {
            val savedSpMoment = spMomentRepository.save(moment)
            logger.info("Saved SpMoment: $savedSpMoment. Now updating user's moments count.")
            //processJustAfterCreation(savedSpMoment, request.taggedProductIds ?: emptySet())
            return savedSpMoment
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateMoment(request: UpdateMomentRequest): SavedSpMomentResponse? {
        try {
            val requestContext = securityProvider.validateRequest()
            val userId = requestContext.getUserIdToUse()

            val moment = getSpMoment(request.momentId) ?: error("Missing moment for momentId: ${request.momentId}")
            if (moment.userId != userId) {
                error("UserId: $userId does not match moment's userId: ${moment.userId}. You are not authorized for this operation")
            }
            val updatedMoment = moment.copy(
                challengeId = request.challengeId,
                momentTaggedUserDetails = request.momentTaggedUserDetails.convertToString(),
                title = request.title,
                description = request.description,
            )
            return saveMoment(updatedMoment)?.toSavedSpMomentResponse()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun deleteMoment(request: DeleteMomentRequest): Boolean {
        try {
            val requestContext = securityProvider.validateRequest()
            val userId = requestContext.getUserIdToUse()

            val moment = getSpMoment(request.momentId) ?: error("Missing moment for momentId: ${request.momentId}")
            if (moment.userId != userId) {
                error("UserId: $userId does not match moment's userId: ${moment.userId}. You are not authorized for this operation")
            }
            spMomentRepository.deleteByMomentId(request.momentId)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getMoment(momentId: String): SavedSpMomentResponse? {
        return (getSpMoment(momentId) ?: error("Missing moment for momentId: $momentId")).toSavedSpMomentResponse()
    }

    fun getMomentsCountByUser(userId: String): SpMomentsCountByUser? {
        TODO("Not yet implemented")
    }
}
