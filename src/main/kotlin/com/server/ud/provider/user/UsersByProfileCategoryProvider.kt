package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByProfileCategoryRepository
import com.server.ud.dao.user.UsersByProfileCategoryTrackerRepository
import com.server.ud.entities.user.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UsersByProfileCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByProfileCategoryRepository: UsersByProfileCategoryRepository

    @Autowired
    private lateinit var usersByProfileCategoryTrackerRepository: UsersByProfileCategoryTrackerRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(userV2: UserV2): List<UsersByProfileCategory> {
        try {
            val usersByProfiles = userV2.getProfiles().profileTypes.map {
                UsersByProfileCategory(
                    profileCategory = it.category,
                    createdAt = userV2.createdAt,
                    userId = userV2.userId,
                )
            }
            logger.info("UsersByProfileCategory saved for userId: ${userV2.userId}")
            val saved = usersByProfileCategoryRepository.saveAll(usersByProfiles)

            usersByProfileCategoryTrackerRepository.saveAll(saved.map { it.toUsersByProfileCategoryTracker() })

            return saved
        } catch (e: Exception) {
            logger.error("Saving UsersByProfileCategory failed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }


    fun getAllUsersTracker(userId: String): List<UsersByProfileCategoryTracker> {
        val limit = 10
        var pagingState = ""

        val trackedUsers = mutableListOf<UsersByProfileCategoryTracker>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val posts = usersByProfileCategoryTrackerRepository.findAllByUserId(
            userId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(posts)
        trackedUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: ""
        while (hasNext) {
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextPosts = usersByProfileCategoryTrackerRepository.findAllByUserId(
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

    fun getAllByUserId(userId: String) : List<UsersByProfileCategory> {
        val trackedUsers = getAllUsersTracker(userId)
        val posts = mutableListOf<UsersByProfileCategory>()
        return trackedUsers.map {
            it.toUsersByProfileCategory()
        }
//        return posts
    }

    fun delete(userId: String) {
        val users = getAllByUserId(userId)
        users.chunked(5).forEach {
            usersByProfileCategoryRepository.deleteAll(it)
        }
    }

}
