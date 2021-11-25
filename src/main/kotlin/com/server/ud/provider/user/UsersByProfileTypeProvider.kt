package com.server.ud.provider.user

import com.server.common.utils.DateUtils
import com.server.ud.dao.user.UsersByProfileTypeRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByProfileType
import com.server.ud.entities.user.getProfiles
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UsersByProfileTypeProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByProfileTypeRepository: UsersByProfileTypeRepository

    fun save(userV2: UserV2): List<UsersByProfileType> {
        try {
            val usersByProfiles = userV2.getProfiles().map {
                UsersByProfileType(
                    profileType = it,
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
            }
            logger.info("UsersByProfileType saved for userId: ${userV2.userId}")
            return usersByProfileTypeRepository.saveAll(usersByProfiles)
        } catch (e: Exception) {
            logger.error("Saving UsersByProfileType filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }
}
