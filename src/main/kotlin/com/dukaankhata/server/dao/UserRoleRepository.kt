package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.User
import com.dukaankhata.server.entities.UserRole
import com.dukaankhata.server.entities.UserRoleKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRoleRepository : JpaRepository<UserRole?, UserRoleKey?> {
    fun findByUser(user: User): List<UserRole>
}
