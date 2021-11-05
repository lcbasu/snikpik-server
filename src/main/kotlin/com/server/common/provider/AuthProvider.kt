package com.server.common.provider

import com.server.common.entities.User
import com.server.common.dao.UserRepository
import com.server.dk.dto.RequestContext
import com.server.dk.dto.PhoneVerificationResponse
import com.server.dk.entities.*
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ReadableIdPrefix
import com.server.common.enums.RoleType
import com.server.common.model.FirebaseAuthUser
import com.server.dk.provider.CompanyProvider
import com.server.dk.provider.EmployeeProvider
import com.server.ud.provider.user.UserV2Provider
import com.twilio.rest.lookups.v1.PhoneNumber
import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var employeeProvider: EmployeeProvider

    @Autowired
    private lateinit var userRoleProvider: UserRoleProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    fun getUser(userId: String): User? =
        try {
            userRepository.findById(userId).get()
        } catch (e: Exception) {
            null
        }

    /**
     * IMPORTANT: Call this very very carefully and only for Public Anonymous User kind of URL requests
     * */
    fun makeSureThePublicRequestHasUserEntity(): User {
        val requestingUser = getRequestUserEntity()
        if (requestingUser == null) {
            val uid = getFirebaseAuthUser()?.getUid() ?: error("User needs to be logged in through phone number or anonymously.")
            logger.warn("Creating user for anonymous user with uid: $uid")
            Sentry.captureMessage("Creating user for anonymous user with uid: $uid")
            return createUser(uid = uid)
        }
        return requestingUser

    }

    fun getFirebaseAuthUser(): FirebaseAuthUser? {
        var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is FirebaseAuthUser) {
            firebaseAuthUserPrincipal = principal as FirebaseAuthUser
        }
        return firebaseAuthUserPrincipal
    }

    fun getRequestUserEntity(): User? {
        val firebaseAuthUserPrincipal = getFirebaseAuthUser()
        val absoluteMobile = firebaseAuthUserPrincipal?.getAbsoluteMobileNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if both are empty then we need to create a new account
        // as this is the case of
        if (absoluteMobile.isBlank() && uid.isBlank()) {
            return null
        }
        var existingUser : User? = if (absoluteMobile.isNotBlank()) userRepository.findByAbsoluteMobile(absoluteMobile) else null
        if (existingUser == null && uid.isNotBlank()) {
            existingUser = userRepository.findByUid(uid)
        }
        return existingUser
    }

    fun registerNotificationSettings(user: User, token: String, tokenProvider: NotificationTokenProvider): User {
        user.notificationToken = token
        user.notificationTokenProvider = tokenProvider
        return userRepository.save(user)
    }

    fun updateUserUid(id: String, uid: String): User {
        return userRepository.let {
            val userOptional = it.findById(id)
            if (userOptional.isPresent) {
                val user = userOptional.get()
                user.uid = uid
                it.save(user)
            } else {
                error("No user found with the id: $id")
            }
        }
    }

    private fun isAnonymous(uid: String? = null, absoluteMobile: String? = null): Boolean {
        return !(uid != null && uid.isNotBlank() && absoluteMobile != null && absoluteMobile.isNotBlank())
    }

    fun getSanitizePhoneNumber(absoluteMobile: String?): String? {
        var sanitizePhoneNumber = absoluteMobile
        if (sanitizePhoneNumber != null) {
            // To long is for removing the leading Zeros
            sanitizePhoneNumber = sanitizePhoneNumber
                .filter { it.isDigit() }
        }
        if (absoluteMobile != null && absoluteMobile.startsWith("+")) {
            sanitizePhoneNumber = "+$sanitizePhoneNumber"
        }
        return sanitizePhoneNumber
    }

    fun createUser(absoluteMobile: String? = null, fullName: String? = null, uid: String? = null): User {
        val sanitizePhoneNumber = getSanitizePhoneNumber(absoluteMobile) ?: ""

        if (sanitizePhoneNumber.isNotBlank() && sanitizePhoneNumber.startsWith("+").not()) {
            error("Phone number has to be with internation format and should start with +")
        }

        if (sanitizePhoneNumber.isNotBlank() && sanitizePhoneNumber.isNotEmpty()) {
            // ensure that the number is unique
            val user = getUserByMobile(sanitizePhoneNumber)
            if (user != null) {
                error("User already has an account for mobile: $sanitizePhoneNumber")
            }
        }

        if (uid != null && uid.isNotBlank() && uid.isNotEmpty()) {
            // ensure that the uis is unique
            val user = getUserByUid(uid)
            if (user != null) {
                error("User already has an account for uid: $uid")
            }
        }

        var countryCode = ""
        if (sanitizePhoneNumber.isNotBlank()) {
            try {
                val verifyPhoneResponse = getVerifiedPhoneResponse(sanitizePhoneNumber)
                if (verifyPhoneResponse.valid.not()) {
                    logger.error("Invalid phone number: $sanitizePhoneNumber")
                } else {
                    // To long is for removing the leading Zeros
                    if (verifyPhoneResponse.valid && verifyPhoneResponse.numberInNationalFormat != null) {
                        val phoneNumberWithOnlyDigits = verifyPhoneResponse.numberInNationalFormat
                            .filter { it.isDigit() }
                            .toLong()
                            .toString()
                        countryCode = sanitizePhoneNumber.replace(phoneNumberWithOnlyDigits, "")
                    }
                }
            } catch (e: NumberFormatException) {
                logger.error("Failed to get country code for $sanitizePhoneNumber")
                e.printStackTrace()
            }
        }

        val newUser = User()
        newUser.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.USR.name)
        newUser.absoluteMobile = sanitizePhoneNumber
        newUser.countryCode = countryCode
        newUser.fullName = fullName
        newUser.uid = uid
        newUser.anonymous = isAnonymous(uid = uid, absoluteMobile = sanitizePhoneNumber)

        // Copy over the details to new user DB in cassandra
        val savedUser = userRepository.save(newUser)
        userV2Provider.save(savedUser)
        return savedUser
    }

    fun getOrCreateUserByPhoneNumber(absoluteMobile: String): User? {
        val sanitizePhoneNumber = getSanitizePhoneNumber(absoluteMobile) ?: return null
        return getUserByMobile(sanitizePhoneNumber) ?:
        createUser(absoluteMobile = sanitizePhoneNumber, fullName = absoluteMobile, uid = "")
    }

    fun getOrCreateUserByUid(uid: String): User? {
        return getUserByUid(uid) ?: createUser(uid = uid)
    }

    fun getUserByMobile(absoluteMobile: String) = userRepository.findByAbsoluteMobile(absoluteMobile)

    fun getUserByUid(uid: String) = userRepository.findByUid(uid)

    // requiredRoleTypes: Any of the role present in the set is ok. So we follow OR and not the AND
    // isPublic Should always be set in the controller
    fun validateRequest(companyServerIdOrUsername: String? = null, employeeId: String? = null, requiredRoleTypes: Set<RoleType> = emptySet()): RequestContext {
        val requestingUser = getRequestUserEntity() ?: error("User is required to be logged in!")
        val requestingUserV2 = userV2Provider.getUser(requestingUser.id) ?: userV2Provider.save(requestingUser) ?: error("Error while getting UserV2 from User")

        var company: Company? = null
        var userRoles: List<UserRole> = emptyList()
        if (companyServerIdOrUsername != null && companyServerIdOrUsername.isNotBlank()) {
            company = companyProvider.getCompanyByServerIdOrUsername(companyServerIdOrUsername) ?: error("Company is required!")
            if (requiredRoleTypes.isNotEmpty()) {
                userRoles = userRoleProvider.getUserRolesForUserAndCompany(
                    user = requestingUser,
                    company = company
                ) ?: emptyList()

                if (userRoles.isEmpty()) {
                    error("Only employers, admin, or employees of the company can perform this operation");
                }

                val currentUserRoles = mutableSetOf<RoleType>()

                userRoles.map {
                    val roleType = it.id?.roleType
                    if (roleType != null) {
                        currentUserRoles.add(RoleType.valueOf(roleType))
                    }
                }

                val common = currentUserRoles.intersect(requiredRoleTypes)

                if (common.isEmpty()) {
                    error("Use doe not have the required access");
                }
            }
        }

        var employee: Employee? = null
        if (employeeId != null && employeeId.isNotBlank()) {
            employee = employeeProvider.getEmployee(employeeId) ?: error("Employee is required")

            // If only employee id is provided
            if (companyServerIdOrUsername == null || companyServerIdOrUsername.isBlank()) {
                company = employee.company

                if (requiredRoleTypes.isNotEmpty()) {
                    userRoles = userRoleProvider.getUserRolesForUserAndCompany(
                        user = requestingUser,
                        company = employee.company!!
                    ) ?: emptyList()

                    if (userRoles.isEmpty()) {
                        error("Only employers, admin, or employees of the company can perform this operation");
                    }

                    val currentUserRoles = mutableSetOf<RoleType>()

                    userRoles.map {
                        val roleType = it.id?.roleType
                        if (roleType != null) {
                            currentUserRoles.add(RoleType.valueOf(roleType))
                        }
                    }

                    val common = currentUserRoles.intersect(requiredRoleTypes)

                    if (common.isEmpty()) {
                        error("Use doe not have the required access");
                    }
                }
            }
        }

        return RequestContext(
            user = requestingUser,
            userV2 = requestingUserV2,
            company = company,
            employee = employee,
            userRoles = userRoles
        )
    }

    fun allAccessRoles(): Set<RoleType> {
        return setOf(RoleType.EMPLOYER, RoleType.EMPLOYEE_ADMIN, RoleType.EMPLOYEE_NON_ADMIN)
    }

    fun onlyAdminLevelRoles(): Set<RoleType> {
        return setOf(RoleType.EMPLOYER, RoleType.EMPLOYEE_ADMIN)
    }

    fun updateUserDefaultAddress(user: User, address: Address): User? {
        try {
            user.defaultAddressId = address.id
            return userRepository.save(user)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getVerifiedPhoneResponse(absoluteMobile: String): PhoneVerificationResponse {
        return try {
            val result = PhoneNumber.fetcher(com.twilio.type.PhoneNumber(absoluteMobile)).fetch()
            PhoneVerificationResponse(
                valid = true,
                countryCode = result.countryCode,
                numberInNationalFormat = result.nationalFormat,
                numberInInterNationalFormat = result.phoneNumber.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
            PhoneVerificationResponse(
                valid = false
            )
        }
    }
}
