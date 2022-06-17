package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByZipcodeRepository
import com.server.ud.dao.user.UsersByZipcodeTrackerRepository
import com.server.ud.entities.user.*
import com.server.common.pagination.CassandraPageV2
import com.server.common.utils.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UsersByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByZipcodeRepository: UsersByZipcodeRepository

    @Autowired
    private lateinit var usersByZipcodeTrackerRepository: UsersByZipcodeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(userV2: UserV2): UsersByZipcode? {
        try {
            if (userV2.permanentLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByZipcode for userId: ${userV2.userId}.")
                return null
            }
            val usersByZipcode = UsersByZipcode(
                zipcode = userV2.permanentLocationZipcode,
                createdAt = userV2.createdAt,
                userId = userV2.userId,
            )
            logger.info("UsersByZipcode saved for userId: ${userV2.userId}")
            val saved = usersByZipcodeRepository.save(usersByZipcode)
            usersByZipcodeTrackerRepository.save(saved.toUsersByZipcodeTracker())
            return saved
        } catch (e: Exception) {
            logger.error("Saving UsersByZipcode failed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return null
        }
    }

    fun getAllUsersTracker(userId: String): List<UsersByZipcodeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UsersByZipcodeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = usersByZipcodeTrackerRepository.findAllByUserId(
            userId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = usersByZipcodeTrackerRepository.findAllByUserId(
                userId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: ""
            trackedUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedUsers
    }

    fun getAllByUserId(userId: String) : List<UsersByZipcode> {
        val trackedUsers = getAllUsersTracker(userId)
        val posts = mutableListOf<UsersByZipcode>()
        return trackedUsers.map {
            it.toUsersByZipcode()
        }
//        return posts
    }

    fun delete(userId: String) {
        val users = getAllByUserId(userId)
        users.chunked(10).forEach {
            usersByZipcodeRepository.deleteAll(it)
        }
    }
}
