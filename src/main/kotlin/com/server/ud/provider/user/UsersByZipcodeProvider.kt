package com.server.ud.provider.user

import com.server.common.utils.DateUtils
import com.server.ud.dao.user.UsersByZipcodeRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByZipcode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UsersByZipcodeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByZipcodeRepository: UsersByZipcodeRepository

    fun save(userV2: UserV2): UsersByZipcode? {
        try {
            if (userV2.userLastLocationZipcode == null) {
                logger.error("zipcode is required to save UsersByZipcode for userId: ${userV2.userId}.")
                return null
            }
            val usersByZipcode = UsersByZipcode(
                zipcode = userV2.userLastLocationZipcode!!,
                forDate = DateUtils.getInstantDate(userV2.createdAt),
                createdAt = userV2.createdAt,
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
            logger.info("Completed")
            return usersByZipcodeRepository.save(usersByZipcode)
        } catch (e: Exception) {
            logger.error("Saving UsersByZipcode filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return null
        }
    }
}
