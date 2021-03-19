package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UserRoleRepository
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.entities.UserRole
import com.dukaankhata.server.entities.UserRoleKey
import com.dukaankhata.server.enums.RoleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserRoleUtils {

    @Autowired
    var userRoleRepository: UserRoleRepository? = null

    fun addUserRole(user: User, company: Company, roleType: RoleType) : UserRole? {

        val key = UserRoleKey()
        key.companyId = company.id
        key.userId = user.id
        key.roleType = roleType.name

        userRoleRepository?.findById(key)?.ifPresent {
            return@ifPresent
        }

        // Save new one as old one does not exist
        return userRoleRepository?.let {
            val userRole = UserRole()
            userRole.company = company
            userRole.user = user
            userRole.id = key
            it.save(userRole)
        }
    }

    fun getUserRolesForUser(user: User) : List<UserRole>? {
        return userRoleRepository?.findByUser(user)
    }

    fun getUserRolesForUserAndCompany(user: User, company: Company) : List<UserRole>? {
        return userRoleRepository?.findByUserAndCompany(user, company)
    }

}
