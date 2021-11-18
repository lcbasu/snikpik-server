package com.server.ud.provider.social

import com.server.common.utils.DateUtils
import com.server.ud.dao.social.FollowersByUserRepository
import com.server.ud.dto.FollowersResponse
import com.server.ud.dto.GetFollowersRequest
import com.server.ud.dto.toSocialRelationResponse
import com.server.ud.entities.social.FollowersByUser
import com.server.ud.entities.user.UserV2
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FollowersByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followersByUserRepository: FollowersByUserRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    // TODO: Optimize this ASAP
    fun getFollowers(userId: String): List<FollowersByUser>? =
        emptyList()
//        try {
//            followersByUserRepository.findAllFollowersForUserId(userId)
//        } catch (e: Exception) {
//            logger.error("Getting Followers for $userId failed.")
//            e.printStackTrace()
//            null
//        }

    fun getFeedForFollowersResponse(request: GetFollowersRequest): FollowersResponse {
        val result = getFeedForFollowers(request)
        return FollowersResponse(
            userId = request.userId,
            followers = result.content?.filterNotNull()?.map { it.toSocialRelationResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getFeedForFollowers(request: GetFollowersRequest): CassandraPageV2<FollowersByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val followers = followersByUserRepository.findAllByUserId(request.userId, pageRequest as Pageable)
        return CassandraPageV2(followers)
    }

    fun save(user: UserV2, follower: UserV2) : FollowersByUser? {
        try {
            val followersByUser = FollowersByUser (
                userId = user.userId,
                forDate = DateUtils.getInstantToday(),
                createdAt = DateUtils.getInstantNow(),
                followerUserId = follower.userId,
                userHandle = user.handle,
                followerHandle = follower.handle,
                userFullName = user.fullName,
                followerFullName = follower.fullName,
            )
            val savedFollowersByUser = followersByUserRepository.save(followersByUser)
            logger.info("Saved FollowersByUser into cassandra for userId: ${user.userId} and followerId: ${follower.userId}")
            return savedFollowersByUser
        } catch (e: Exception) {
            logger.info("Failed: Saving FollowersByUser into cassandra for userId: ${user.userId} and followerId: ${follower.userId}")
            e.printStackTrace()
            return null
        }
    }
}
