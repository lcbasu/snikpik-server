package com.server.dk.dao

import com.server.dk.entities.UniqueId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UniqueIdRepository : JpaRepository<UniqueId?, String?>
