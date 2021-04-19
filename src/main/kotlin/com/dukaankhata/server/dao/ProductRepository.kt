package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductRepository : JpaRepository<Product?, String?>
