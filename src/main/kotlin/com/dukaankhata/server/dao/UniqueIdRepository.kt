package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.UniqueId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UniqueIdRepository : JpaRepository<UniqueId?, String?>
