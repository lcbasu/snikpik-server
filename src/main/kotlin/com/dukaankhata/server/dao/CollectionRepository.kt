package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Collection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CollectionRepository : JpaRepository<Collection?, String?>
