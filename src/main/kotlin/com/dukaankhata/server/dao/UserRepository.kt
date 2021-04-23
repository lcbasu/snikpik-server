package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<User?, String?> {
    fun findByUid(uid: String): User?
    fun findByMobile(mobile: String): User?
}
