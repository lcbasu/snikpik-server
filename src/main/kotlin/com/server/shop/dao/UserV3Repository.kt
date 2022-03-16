package com.server.shop.dao

import com.server.shop.entities.UserV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserV3Repository : JpaRepository<UserV3?, String?> {
}
