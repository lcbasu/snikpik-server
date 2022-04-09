package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByZipcodeAndProfileTypeRepository
import com.server.ud.dao.user.UsersByZipcodeAndProfileTypeTrackerRepository
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.entities.user.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UsersByZipcodeAndProfileTypeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByZipcodeAndProfileTypeRepository: UsersByZipcodeAndProfileTypeRepository

    @Autowired
    private lateinit var usersByZipcodeAndProfileTypeTrackerRepository: UsersByZipcodeAndProfileTypeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): CassandraPageV2<UsersByZipcodeAndProfileType> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val users = usersByZipcodeAndProfileTypeRepository.findAllByZipcodeAndProfileType(request.zipcode, request.profileType, pageRequest as Pageable)
        return CassandraPageV2(users)
    }

    fun save(userV2: UserV2): List<UsersByZipcodeAndProfileType> {
        try {
            if (userV2.permanentLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByZipcodeAndProfileType for userId: ${userV2.userId}.")
                return emptyList()
            }
            val usersByZipcodeAndProfileType = userV2.getProfiles().profileTypes.map {
                UsersByZipcodeAndProfileType(
                    zipcode = userV2.permanentLocationZipcode!!,
                    profileType = it.id,
                    userId = userV2.userId,
                )
            }
            val saved =  usersByZipcodeAndProfileTypeRepository.saveAll(usersByZipcodeAndProfileType)
            usersByZipcodeAndProfileTypeTrackerRepository.saveAll(saved.map { it.toUsersByZipcodeAndProfileTypeTracker() })
            return saved
        } catch (e: Exception) {
            logger.error("Saving UsersByZipcodeAndProfileType filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }

//    fun delete(userId: String) {
//        usersByZipcodeAndProfileTypeRepository.deleteAll(usersByZipcodeAndProfileTypeRepository.findAllByUserId(userId))
//    }

    fun getAllUsersTracker(userId: String): List<UsersByZipcodeAndProfileTypeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UsersByZipcodeAndProfileTypeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = usersByZipcodeAndProfileTypeTrackerRepository.findAllByUserId(
            userId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = usersByZipcodeAndProfileTypeTrackerRepository.findAllByUserId(
                userId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedUsers
    }

    fun getAllByUserId(userId: String) : List<UsersByZipcodeAndProfileType> {
        val trackedUsers = getAllUsersTracker(userId)
        val posts = mutableListOf<UsersByZipcodeAndProfileType>()
        return trackedUsers.map {
            it.toUsersByZipcodeAndProfileType()
        }
//        return posts
    }

    fun delete(userId: String) {
        val users = getAllByUserId(userId)
        users.chunked(10).forEach {
            usersByZipcodeAndProfileTypeRepository.deleteAll(it)
        }
    }

}
