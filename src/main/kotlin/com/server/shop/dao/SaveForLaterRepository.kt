package com.server.shop.dao

import com.server.shop.entities.SaveForLater
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveForLaterRepository : JpaRepository<SaveForLater?, String?> {
}
