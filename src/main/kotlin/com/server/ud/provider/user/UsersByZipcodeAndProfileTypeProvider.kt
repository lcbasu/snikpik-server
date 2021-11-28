package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByZipcodeAndProfileTypeRepository
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByZipcodeAndProfileType
import com.server.ud.entities.user.getProfiles
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
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun getFeedForMarketplaceUsers(request: MarketplaceUserFeedRequest): CassandraPageV2<UsersByZipcodeAndProfileType> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val users = usersByZipcodeAndProfileTypeRepository.findAllByZipcodeAndProfileType(request.zipcode, request.profileType, pageRequest as Pageable)
        return CassandraPageV2(users)
    }

    fun save(userV2: UserV2): List<UsersByZipcodeAndProfileType> {
        try {
            if (userV2.userLastLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByZipcodeAndProfileType for userId: ${userV2.userId}.")
                return emptyList()
            }
            val usersByZipcodeAndProfileType = userV2.getProfiles().profileTypes.map {
                UsersByZipcodeAndProfileType(
                    zipcode = userV2.userLastLocationZipcode!!,
                    profileType = it.id,
                    userId = userV2.userId,
                )
            }
            return usersByZipcodeAndProfileTypeRepository.saveAll(usersByZipcodeAndProfileType)
        } catch (e: Exception) {
            logger.error("Saving UsersByZipcodeAndProfileType filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }
}
