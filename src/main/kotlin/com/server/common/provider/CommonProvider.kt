package com.server.common.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.utils.CommonUtils
import com.server.ud.utils.UDCommonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CommonProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    var MIN_HANDLE_LENGTH = 4

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Transactional
    fun isUsernameHandleAvailable(handle: String): Boolean {
        val username = CommonUtils.getLowercaseUsername(handle)
        logger.info("username: $username")
        if ((handle == username).not()) {
            logger.error("Handle not allowed. valid username: $username but input handle is: $handle")
            return false
        }
        if (handle.length < MIN_HANDLE_LENGTH) {
            return false
        }
        // Blocked locked usernames
        if (UDCommonUtils.lockedUsernames().map { it.toLowerCase() }.contains(handle.toLowerCase())) {
            return false
        }
        return uniqueIdProvider.isIdAvailable(handle)
    }

    /**
     *
     * Disabling the transaction here as any try catch block will cause the transaction to rollback
     * */
//    @Transactional
    fun getUsernameFromName(name: String, prefixFor: ReadableIdPrefix): String {
        val prefix = getUsernamePrefixFromName(name)
        var currentCount = 0
        val maxTryOutCount = 10
        while (currentCount < maxTryOutCount) {
            currentCount += 1
            val currentUsername = try {
                uniqueIdProvider.getUniqueIdWithoutSaving(
                    prefix = prefix,
                    minLength = prefix.length,
                    maxLength = prefix.length + currentCount)
            } catch (e: Exception) {
                ""
            }
            if (currentUsername.isNotEmpty()) {
                uniqueIdProvider.saveId(currentUsername, prefixFor.name)
                return currentUsername
            }
        }
        error("Can not generate a suitable username for $name")
    }

    private fun getUsernamePrefixFromName(name: String): String {
        // Keep maximum length to be some constant
        val maxLengthOfAutomatedPrefix = 10
        return CommonUtils.getLowercaseStringWithOnlyCharOrDigit(name).take(maxLengthOfAutomatedPrefix)
    }

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun hardCheckForAdmin(){
        val firebaseAuthUser = securityProvider.validateRequest()
        val loggedInUserId = firebaseAuthUser.getUserIdToUse()
        val isAdmin = UDCommonUtils.isAdmin(loggedInUserId)
        if (isAdmin.not()) {
            val message = "User $loggedInUserId is not authorized for this operation. Only admins can perform these operations."
            logger.error(message)
            error(message)
        }
    }
}
