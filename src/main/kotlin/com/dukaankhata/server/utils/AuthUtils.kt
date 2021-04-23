package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.dto.RequestContext
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.model.FirebaseAuthUser
import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

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
        return userRepository.findByMobile(mobile) ?: userRepository.findByUid(uid)
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

    fun createUser(phoneNumber: String? = null, fullName: String? = null, uid: String? = null): User {

        if (phoneNumber != null && phoneNumber.isNotBlank() && phoneNumber.isNotEmpty()) {
            // ensure that the number is unique
            val user = getUserByMobile(phoneNumber)
            if (user != null) {
                error("User already has an account for mobile: $phoneNumber")
            }
        }

        if (uid != null && uid.isNotBlank() && uid.isNotEmpty()) {
            // ensure that the uis is unique
            val user = getUserByUid(uid)
            if (user != null) {
                error("User already has an account for uid: $uid")
            }
        }

        val newUser = User()
        newUser.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.USR.name)
        newUser.mobile = phoneNumber
        newUser.fullName = fullName
        newUser.uid = uid

        return userRepository.save(newUser)
    }

    fun getOrCreateUserByPhoneNumber(phoneNumber: String): User? {
        return getUserByMobile(phoneNumber) ?:
        createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = "")
    }

    fun getOrCreateUserByUid(uid: String): User? {
        return getUserByUid(uid) ?: createUser(uid = uid)
    }

    fun getUserByMobile(mobile: String) = userRepository.findByMobile(mobile)

    fun getUserByUid(uid: String) = userRepository.findByUid(uid)

    // requiredRoleTypes: Any of the role present in the set is ok. So we follow OR and not the AND
    // isPublic Should always be set in the controller
    fun validateRequest(companyId: Long = -1, employeeId: Long = -1, requiredRoleTypes: Set<RoleType> = emptySet()): RequestContext {
        val requestingUser = getRequestUserEntity() ?: error("User is required to be logged in!")

        var company: Company? = null
        var userRoles: List<UserRole> = emptyList()
        if (companyId > 0) {
            company = companyUtils.getCompany(companyId)
            if (company == null) {
                error("Company is required!");
            }

            userRoles = userRoleUtils.getUserRolesForUserAndCompany(
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

        var employee: Employee? = null
        if (employeeId > 0) {
            employee = employeeUtils.getEmployee(employeeId)
            if (employee == null) {
                error("Employee is required");
            }

            // If only employee id is provided
            if (companyId <= 0) {
                company = employee.company

                userRoles = userRoleUtils.getUserRolesForUserAndCompany(
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
}
