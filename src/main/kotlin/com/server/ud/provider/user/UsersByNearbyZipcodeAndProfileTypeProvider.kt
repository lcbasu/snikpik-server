package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByNearbyZipcodeAndProfileTypeRepository
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileType
import com.server.ud.entities.user.getProfiles
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UsersByNearbyZipcodeAndProfileTypeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByNearbyZipcodeAndProfileTypeRepository: UsersByNearbyZipcodeAndProfileTypeRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(nearbyPostsUsers: List<UsersByNearbyZipcodeAndProfileType>, forNearbyZipcode: String): List<UsersByNearbyZipcodeAndProfileType> {
        try {
            val users = nearbyPostsUsers.map { user ->
                user.copy(zipcode = forNearbyZipcode)
            }
            return usersByNearbyZipcodeAndProfileTypeRepository.saveAll(users)
        } catch (e: Exception) {
            logger.error("Saving UsersByNearbyZipcodeAndProfileType failed forNearbyZipcode $forNearbyZipcode.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun save(userV2: UserV2, nearbyZipcodes: Set<String>): List<UsersByNearbyZipcodeAndProfileType> {
        try {
            if (userV2.userLastLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByNearbyZipcodeAndProfileType for userId: ${userV2.userId}.")
                return emptyList()
            }
            val usersByNearbyZipcodeAndProfileType = nearbyZipcodes.map { nearbyZipcode ->
                userV2.getProfiles().profileTypes.map { profileTypeResponse ->
                    UsersByNearbyZipcodeAndProfileType(
                        zipcode = nearbyZipcode,
                        profileType = profileTypeResponse.id,
                        userId = userV2.userId,
                        originalZipcode = userV2.userLastLocationZipcode,
                    )
                }
            }.flatten()
            return usersByNearbyZipcodeAndProfileTypeRepository.saveAll(usersByNearbyZipcodeAndProfileType)
        } catch (e: Exception) {
            logger.error("Saving UsersByNearbyZipcodeAndProfileType failed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): CassandraPageV2<UsersByNearbyZipcodeAndProfileType> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val users = usersByNearbyZipcodeAndProfileTypeRepository.findAllByZipcodeAndProfileType(request.zipcode, request.profileType, pageRequest as Pageable)
        return CassandraPageV2(users)
    }
}
