package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.dto.RequestContext
import com.dukaankhata.server.dto.VerifyPhoneResponse
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.model.FirebaseAuthUser
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
        val mobile = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if both are empty then we need to create a new account
        // as this is the case of
        if (mobile.isBlank() && uid.isBlank()) {
            return null
        }
        var existingUser : User? = if (mobile.isNotBlank()) userRepository.findByMobile(mobile) else null
        if (existingUser == null && uid.isNotBlank()) {
            existingUser = userRepository.findByUid(uid)
        }
        return existingUser
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

    private fun isAnonymous(uid: String? = null, phoneNumber: String? = null): Boolean {
        return !(uid != null && uid.isNotBlank() && phoneNumber != null && phoneNumber.isNotBlank())
    }

    fun getSanitizePhoneNumber(phoneNumber: String?): String? {
        var sanitizePhoneNumber = phoneNumber
        if (sanitizePhoneNumber != null) {
            // To long is for removing the leading Zeros
            sanitizePhoneNumber = sanitizePhoneNumber
                .filter { it.isDigit() }
        }
        if (phoneNumber != null && phoneNumber.startsWith("+")) {
            sanitizePhoneNumber = "+$sanitizePhoneNumber"
        }
        return sanitizePhoneNumber
    }

    fun createUser(phoneNumber: String? = null, fullName: String? = null, uid: String? = null): User {
        val sanitizePhoneNumber = getSanitizePhoneNumber(phoneNumber) ?: ""

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
        newUser.mobile = sanitizePhoneNumber
        newUser.countryCode = countryCode
        newUser.fullName = fullName
        newUser.uid = uid
        newUser.anonymous = isAnonymous(uid = uid, phoneNumber = sanitizePhoneNumber)

        return userRepository.save(newUser)
    }

    fun getOrCreateUserByPhoneNumber(phoneNumber: String): User? {
        val sanitizePhoneNumber = getSanitizePhoneNumber(phoneNumber) ?: return null
        return getUserByMobile(sanitizePhoneNumber) ?:
        createUser(phoneNumber = sanitizePhoneNumber, fullName = phoneNumber, uid = "")
    }

    fun getOrCreateUserByUid(uid: String): User? {
        return getUserByUid(uid) ?: createUser(uid = uid)
    }

    fun getUserByMobile(mobile: String) = userRepository.findByMobile(mobile)

    fun getUserByUid(uid: String) = userRepository.findByUid(uid)

    // requiredRoleTypes: Any of the role present in the set is ok. So we follow OR and not the AND
    // isPublic Should always be set in the controller
    fun validateRequest(companyId: String? = null, employeeId: String? = null, requiredRoleTypes: Set<RoleType> = emptySet()): RequestContext {
        val requestingUser = getRequestUserEntity() ?: error("User is required to be logged in!")

        var company: Company? = null
        var userRoles: List<UserRole> = emptyList()
        if (companyId != null && companyId.isNotBlank()) {
            company = companyProvider.getCompany(companyId) ?: error("Company is required!")

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
            if (companyId == null || companyId.isBlank()) {
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

    fun getVerifiedPhoneResponse(phoneNumber: String): VerifyPhoneResponse {
        return try {
            val result = PhoneNumber.fetcher(com.twilio.type.PhoneNumber(phoneNumber)).fetch()
            VerifyPhoneResponse(
                valid = true,
                countryCode = result.countryCode,
                numberInNationalFormat = result.nationalFormat,
                numberInInterNationalFormat = result.phoneNumber.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
            VerifyPhoneResponse(
                valid = false
            )
        }
    }
}