package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByProfileTypeRepository
import com.server.ud.dao.user.UsersByProfileTypeTrackerRepository
import com.server.ud.entities.user.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UsersByProfileTypeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByProfileTypeRepository: UsersByProfileTypeRepository

    @Autowired
    private lateinit var usersByProfileTypeTrackerRepository: UsersByProfileTypeTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(userV2: UserV2): List<UsersByProfileType> {
        try {
            val usersByProfiles = userV2.getProfiles().profileTypes.map {
                UsersByProfileType(
                    profileType = it.id,
                    createdAt = userV2.createdAt,
                    userId = userV2.userId,
                )
            }
            logger.info("UsersByProfileType saved for userId: ${userV2.userId}")
            val saved = usersByProfileTypeRepository.saveAll(usersByProfiles)

            usersByProfileTypeTrackerRepository.saveAll(saved.map { it.toUsersByProfileTypeTracker() })

            return saved
        } catch (e: Exception) {
            logger.error("Saving UsersByProfileType filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }


    fun getAllUsersTracker(userId: String): List<UsersByProfileTypeTracker> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UsersByProfileTypeTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = usersByProfileTypeTrackerRepository.findAllByUserId(
            userId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = usersByProfileTypeTrackerRepository.findAllByUserId(
                userId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextPosts)
            hasNext = nextSlicedResult.hasNext == true
            trackedUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedUsers
    }

    fun getAllByUserId(userId: String) : List<UsersByProfileType> {
        val trackedUsers = getAllUsersTracker(userId)
        val posts = mutableListOf<UsersByProfileType>()
        return trackedUsers.map {
            it.toUsersByProfileType()
        }
//        return posts
    }
    fun delete(userId: String) {
        val users = getAllByUserId(userId)
        users.chunked(10).forEach {
            usersByProfileTypeRepository.deleteAll(it)
        }
    }
}
