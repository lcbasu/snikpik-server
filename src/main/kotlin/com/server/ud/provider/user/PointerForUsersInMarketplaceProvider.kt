package com.server.ud.provider.user

import com.server.common.enums.ProfileType
import com.server.common.enums.UserPositionInMarketplace
import com.server.ud.dao.user.PointerForUsersInMarketplaceRepository
import com.server.ud.entities.user.PointerForUsersInMarketplace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PointerForUsersInMarketplaceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var pointerForUsersInMarketplaceRepository: PointerForUsersInMarketplaceRepository

    fun getExistingEntryPosition(profileType: ProfileType, zipcode: String): PointerForUsersInMarketplace? =
        try {
            val positionData = pointerForUsersInMarketplaceRepository.findAllForGivenData(
                profileCategory = profileType.category,
                profileType = profileType,
                zipcode = zipcode
            )
            if (positionData.size > 1) {
                error("More than one PointerForUsersInMarketplace present for profileType: ${profileType}")
            }
            positionData.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting PointerForUsersInMarketplace for profileType: ${profileType} failed.")
            e.printStackTrace()
            null
        }

    fun getPositionToUseNow(profileType: ProfileType, zipcode: String): UserPositionInMarketplace {
        val existing = getExistingEntryPosition(profileType, zipcode) ?: return UserPositionInMarketplace.ONE
        return if (existing.lastPosition == UserPositionInMarketplace.ONE) {
            UserPositionInMarketplace.TWO
        } else {
            UserPositionInMarketplace.ONE
        }
    }

    fun update(profileType: ProfileType, zipcode: String): PointerForUsersInMarketplace? {
        try {
            val existing = getExistingEntryPosition(profileType, zipcode)
            val newPointerForUsersInMarketplace = if (existing == null) {
                PointerForUsersInMarketplace(
                    profileCategory = profileType.category,
                    profileType = profileType,
                    zipcode = zipcode,
                    lastPosition = UserPositionInMarketplace.ONE,
                )
            } else {
                // In case of existing position,
                // use next position compared to the lats saved position
                val lastPosition = if (existing.lastPosition == UserPositionInMarketplace.ONE) {
                    UserPositionInMarketplace.TWO
                } else {
                    UserPositionInMarketplace.ONE
                }
                PointerForUsersInMarketplace(
                    profileCategory = profileType.category,
                    profileType = profileType,
                    zipcode = zipcode,
                    lastPosition = lastPosition,
                )
            }
            return pointerForUsersInMarketplaceRepository.save(newPointerForUsersInMarketplace)
        } catch (e: Exception) {
            logger.error("Saving PointerForUsersInMarketplace filed for zipcode: ${zipcode}.")
            e.printStackTrace()
            return null
        }
    }
}
