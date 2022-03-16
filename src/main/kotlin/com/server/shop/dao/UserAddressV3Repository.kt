package com.server.shop.dao

import com.server.shop.entities.UserAddressKeyV3
import com.server.shop.entities.UserAddressV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAddressV3Repository : JpaRepository<UserAddressV3?, UserAddressKeyV3?> {
    fun findAllByUserId(userId: String): List<UserAddressV3>
}
