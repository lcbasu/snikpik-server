package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.User
import com.dukaankhata.server.entities.UserAddress
import com.dukaankhata.server.entities.UserAddressKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository : JpaRepository<UserAddress?, UserAddressKey?> {
    fun findAllByUser(user: User): List<UserAddress>
}
