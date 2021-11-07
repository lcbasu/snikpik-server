package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByZipcodeAndProfileCategoryRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByZipcodeAndProfileCategory
import com.server.ud.entities.user.getProfiles
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UsersByZipcodeAndProfileProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByZipcodeAndProfileCategoryRepository: UsersByZipcodeAndProfileCategoryRepository

    @Autowired
    private lateinit var pointerForUsersInMarketplaceProvider: PointerForUsersInMarketplaceProvider

    fun save(userV2: UserV2): List<UsersByZipcodeAndProfileCategory> {
        try {
            if (userV2.userLastLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByZipcodeAndProfileCategory for userId: ${userV2.userId}.")
                return emptyList()
            }
            val users = userV2.getProfiles().map {
                val positionToUseNow = pointerForUsersInMarketplaceProvider.getPositionToUseNow(it, userV2.userLastLocationZipcode!!)
                UsersByZipcodeAndProfileCategory(
                    zipcode = userV2.userLastLocationZipcode!!,
                    profileCategory = it.category,
                    profileType = it,
                    position = positionToUseNow,
                    userId = userV2.userId,
                    absoluteMobile = userV2.absoluteMobile,
                    countryCode = userV2.countryCode,
                    handle = userV2.handle,
                    dp = userV2.dp,
                    uid = userV2.uid,
                    anonymous = userV2.anonymous,
                    verified = userV2.verified,
                    profiles = userV2.profiles,
                    fullName = userV2.fullName,
                )
            }
            logger.info("Completed")
            return usersByZipcodeAndProfileCategoryRepository.saveAll(users)
        } catch (e: Exception) {
            logger.error("Saving UsersByZipcodeAndProfileCategory filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }
}
