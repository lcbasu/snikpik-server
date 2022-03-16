package com.server.shop.dao

import com.server.shop.entities.CompanyV3
import com.server.shop.entities.UserV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CompanyV3Repository : JpaRepository<CompanyV3?, String?> {
    fun findByAddedBy(user: UserV3): List<CompanyV3>
}
