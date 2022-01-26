package com.server.ud.provider.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.AuthProvider
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.user.UserV2ByMobileNumberRepository
import com.server.ud.entities.user.UserV2ByMobileNumber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV2ByMobileNumberProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2ByMobileNumberRepository: UserV2ByMobileNumberRepository

    @Autowired
    private lateinit var authProvider: AuthProvider

    fun getUserV2ByMobileNumber(absoluteMobileNumber: String): UserV2ByMobileNumber? =
        try {
            val users = userV2ByMobileNumberRepository.findAllByAbsoluteMobile(absoluteMobileNumber)
            if (users.size > 1) {
                error("More than one user has same mobileNumber: $absoluteMobileNumber")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting UserV2ByMobileNumber for $absoluteMobileNumber failed.")
            e.printStackTrace()
            null
        }

//    fun saveNewUserV2ByMobileNumber(absoluteMobileNumber: String) : UserV2ByMobileNumber? {
//        try {
//            return saveUserV2ByMobileNumber(
//                absoluteMobileNumber = absoluteMobileNumber,
//                userId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.USR.name)
//            )
//        } catch (e: Exception) {
//            logger.error("Saving UserV2ByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber failed.")
//            e.printStackTrace()
//            return null
//        }
//    }

    fun getOrSaveUserV2ByMobileNumber(absoluteMobileNumber: String) : UserV2ByMobileNumber? {
        try {
            val existing = getUserV2ByMobileNumber(absoluteMobileNumber)
            if (existing != null) {
                return existing
            }
            return saveUserV2ByMobileNumber(
                absoluteMobileNumber = absoluteMobileNumber,
                userId = authProvider.saveNewUserToFirebase(absoluteMobileNumber))
        } catch (e: Exception) {
            logger.error("Saving UserV2ByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber failed.")
            e.printStackTrace()
            return null
        }
    }

    fun saveUserV2ByMobileNumber(absoluteMobileNumber: String, userId: String) : UserV2ByMobileNumber? {
        try {
            if (absoluteMobileNumber.isBlank()) {
                error("absoluteMobileNumber is blank.")
            }
            val existing = getUserV2ByMobileNumber(absoluteMobileNumber)
            if (existing != null) {
                if (existing.userId != userId) {
                    error("UserV2ByMobileNumber already exists for $absoluteMobileNumber but with a different userId. Trying to save with $userId but the saved userId is: ${existing.userId}")
                } else {
                    return existing
                }
            }
            return userV2ByMobileNumberRepository.save(UserV2ByMobileNumber(
                absoluteMobile = absoluteMobileNumber,
                userId = userId
            ))
        } catch (e: Exception) {
            logger.error("Saving UserV2ByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber and userId: $userId failed.")
            e.printStackTrace()
            return null
        }
    }

}
