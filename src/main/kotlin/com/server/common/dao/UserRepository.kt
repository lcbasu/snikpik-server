package com.server.common.dao

import com.server.common.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<User?, String?> {
    fun findByUid(uid: String): User?
    fun findByAbsoluteMobile(absoluteMobile: String): User?
}
