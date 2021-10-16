package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.User
import com.server.dk.entities.UserRole
import com.server.dk.entities.UserRoleKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRoleRepository : JpaRepository<UserRole?, UserRoleKey?> {
    fun findByUser(user: User): List<UserRole>
    fun findByUserAndCompany(user: User, company: Company): List<UserRole>
}
