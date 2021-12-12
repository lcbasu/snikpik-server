package com.server.ud.provider.user

import com.server.ud.dao.user.ProfileTypesByNearbyZipcodeAndProfileCategoryRepository
import com.server.ud.dto.MarketplaceProfileTypesFeedRequest
import com.server.ud.entities.user.ProfileTypesByNearbyZipcode
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getProfiles
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProfileTypesByNearbyZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repository: ProfileTypesByNearbyZipcodeAndProfileCategoryRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun save(nearbyPostsUsers: List<ProfileTypesByNearbyZipcode>, forNearbyZipcode: String): List<ProfileTypesByNearbyZipcode> {
        try {
            val users = nearbyPostsUsers.map { user ->
                user.copy(zipcode = forNearbyZipcode)
            }
            return repository.saveAll(users)
        } catch (e: Exception) {
            logger.error("Saving ProfileTypesByNearbyZipcodeAndProfileCategory failed forNearbyZipcode $forNearbyZipcode.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun save(userV2: UserV2, nearbyZipcodes: Set<String>): List<ProfileTypesByNearbyZipcode> {
        try {
            if (userV2.permanentLocationZipcode == null) {
                logger.error("zipcode is required to save ProfileTypesByNearbyZipcodeAndProfileCategory for userId: ${userV2.userId}.")
                return emptyList()
            }
            val usersByNearbyZipcodeAndProfileType = nearbyZipcodes.map { nearbyZipcode ->
                userV2.getProfiles().profileTypes.map { profileTypeResponse ->
                    ProfileTypesByNearbyZipcode(
                        zipcode = nearbyZipcode,
                        profileCategory = profileTypeResponse.category,
                        profileType = profileTypeResponse.id,
                        originalZipcode = userV2.permanentLocationZipcode,
                    )
                }
            }.flatten()
            return repository.saveAll(usersByNearbyZipcodeAndProfileType)
        } catch (e: Exception) {
            logger.error("Saving ProfileTypesByNearbyZipcodeAndProfileCategory failed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }

    fun getFeedForMarketplaceProfileTypes(request: MarketplaceProfileTypesFeedRequest): CassandraPageV2<ProfileTypesByNearbyZipcode> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val profiles = repository.findAllByZipcodeAndProfileCategory(request.zipcode, request.profileCategory, pageRequest as Pageable)
        return CassandraPageV2(profiles)
    }
}
