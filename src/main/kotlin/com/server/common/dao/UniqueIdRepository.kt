package com.server.common.dao

import com.server.common.entities.UniqueId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UniqueIdRepository : JpaRepository<UniqueId?, String?>
