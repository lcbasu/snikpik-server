package com.server.dk.dao

import com.server.dk.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<User?, String?> {
    fun findByUid(uid: String): User?
    fun findByAbsoluteMobile(absoluteMobile: String): User?
}
