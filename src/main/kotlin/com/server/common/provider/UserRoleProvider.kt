package com.server.common.provider

import com.server.dk.dao.UserRoleRepository
import com.server.dk.entities.Company
import com.server.dk.entities.User
import com.server.dk.entities.UserRole
import com.server.dk.entities.UserRoleKey
import com.server.common.enums.RoleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserRoleProvider {

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
            userRole.user = company.user
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
