package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.ProductCollection
import com.dukaankhata.server.entities.ProductCollectionKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductCollectionRepository : JpaRepository<ProductCollection?, ProductCollectionKey?>
