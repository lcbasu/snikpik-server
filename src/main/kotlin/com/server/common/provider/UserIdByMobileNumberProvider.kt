package com.server.common.provider

import com.server.common.dao.UserIdByMobileNumberRepository
import com.server.common.entities.UserIdByMobileNumber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserIdByMobileNumberProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userIdByMobileNumberRepository: UserIdByMobileNumberRepository

    @Autowired
    private lateinit var authProvider: AuthProvider

    fun getUserIdByMobileNumber(absoluteMobileNumber: String): UserIdByMobileNumber? =
        try {
            val users = userIdByMobileNumberRepository.findAllByAbsoluteMobile(absoluteMobileNumber)
            if (users.size > 1) {
                error("More than one user has same mobileNumber: $absoluteMobileNumber")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting UserIdByMobileNumber for $absoluteMobileNumber failed.")
            e.printStackTrace()
            null
        }

//    fun saveNewUserIdByMobileNumber(absoluteMobileNumber: String) : UserIdByMobileNumber? {
//        try {
//            return saveUserIdByMobileNumber(
//                absoluteMobileNumber = absoluteMobileNumber,
//                userId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.USR.name)
//            )
//        } catch (e: Exception) {
//            logger.error("Saving UserIdByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber failed.")
//            e.printStackTrace()
//            return null
//        }
//    }

    fun getOrSaveUserIdByMobileNumber(absoluteMobileNumber: String) : UserIdByMobileNumber? {
        try {
            val existing = getUserIdByMobileNumber(absoluteMobileNumber)
            if (existing != null) {
                return existing
            }
            return saveUserIdByMobileNumber(
                absoluteMobileNumber = absoluteMobileNumber,
                userId = authProvider.saveNewUserToFirebase(absoluteMobileNumber))
        } catch (e: Exception) {
            logger.error("Saving UserIdByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber failed.")
            e.printStackTrace()
            return null
        }
    }

    fun saveUserIdByMobileNumber(absoluteMobileNumber: String, userId: String) : UserIdByMobileNumber? {
        try {
            if (absoluteMobileNumber.isBlank()) {
                error("absoluteMobileNumber is blank.")
            }
            val existing = getUserIdByMobileNumber(absoluteMobileNumber)
            if (existing != null) {
                if (existing.userId != userId) {
                    error("UserIdByMobileNumber already exists for $absoluteMobileNumber but with a different userId. Trying to save with $userId but the saved userId is: ${existing.userId}")
                } else {
                    return existing
                }
            }
            return userIdByMobileNumberRepository.save(
                UserIdByMobileNumber(
                absoluteMobile = absoluteMobileNumber,
                userId = userId
            )
            )
        } catch (e: Exception) {
            logger.error("Saving UserIdByMobileNumber for absoluteMobileNumber: $absoluteMobileNumber and userId: $userId failed.")
            e.printStackTrace()
            return null
        }
    }

}
