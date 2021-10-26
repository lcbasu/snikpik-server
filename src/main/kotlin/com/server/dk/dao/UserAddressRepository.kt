package com.server.dk.dao

import com.server.common.entities.User
import com.server.dk.entities.UserAddress
import com.server.dk.entities.UserAddressKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository : JpaRepository<UserAddress?, UserAddressKey?> {
    fun findAllByUser(user: User): List<UserAddress>
}
