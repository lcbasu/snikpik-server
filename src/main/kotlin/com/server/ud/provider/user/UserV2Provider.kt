package com.server.ud.provider.user

import com.server.common.dao.UserRepository
import com.server.common.entities.User
import com.server.common.utils.DateUtils
import com.server.ud.dao.user.UserV2Repository
import com.server.ud.entities.user.UserV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV2Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Repository: UserV2Repository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun getUser(userId: String): UserV2? =
        try {
            userV2Repository.findById(userId).get()
        } catch (e: Exception) {
            logger.error("Getting UserV2 for $userId failed.")
            null
        }

    fun createForAll() {
        userRepository.findAll().toList().filterNotNull().map {
            save(it)
        }
    }


    fun save(user: User) : UserV2? {
        try {
            val userV2 = UserV2(
                userId = user.id,
                createdAt = DateUtils.toDate(user.createdAt).toInstant(),
                absoluteMobile = user.absoluteMobile,
                countryCode = user.countryCode,
                handle = null,
                uid = user.uid,
                anonymous = user.anonymous,
                verified = false,
                profession = null,
                fullName = user.fullName,
                notificationToken = user.notificationToken,
                notificationTokenProvider = user.notificationTokenProvider,
                userLastLocationZipcode = null,
                userLastGooglePlaceId = null,
                userLastLocationId = null,
                userLastLocationName = null,
                userLastLocationLat = null,
                userLastLocationLng = null,
            )
            return userV2Repository.save(userV2)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
