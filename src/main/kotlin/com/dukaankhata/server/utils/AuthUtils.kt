package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.dto.RequestContext
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.Gender
import com.dukaankhata.server.enums.RoleType
import com.dukaankhata.server.model.FirebaseAuthUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

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
        val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        userRepository.let {
            val user = it.findById(phoneNumber)
            if (user.isPresent && user.get().id.isNotEmpty()) {
                return user.get()
            }
        }
        return null
    }

    fun updateUserUid(id: String, uid: String): User? {
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

    fun createUser(phoneNumber: String, fullName: String, uid: String): User? {
        return userRepository.let { val newUser = User()
            newUser.id = phoneNumber
            newUser.fullName = fullName
            newUser.gender = Gender.MALE
            newUser.uid = uid
            it.save(newUser)
        }
    }

    fun getOrCreateUserByPhoneNumber(phoneNumber: String): User? {
        return userRepository.let {
            return getUserByPhoneNumber(phoneNumber) ?:
            createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = "")
        }
    }

    fun getUserByPhoneNumber(phoneNumber: String): User? {
        return userRepository.let {
            val userOptional = it.findById(phoneNumber)
            return if (userOptional.isPresent && userOptional.get().id.isNotEmpty()) {
                userOptional.get()
            } else {
                null
            }
        }
    }


    // requiredRoleTypes: Any of the role present in the set is ok. So we follow OR and not the AND
    fun validateRequest(companyId: Long = -1, employeeId: Long = -1, requiredRoleTypes: Set<RoleType> = emptySet()): RequestContext {
        val requestingUser = getRequestUserEntity()

        if (requestingUser == null) {
            error("User is required to be logged in!");
        }

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
