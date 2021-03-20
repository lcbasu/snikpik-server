package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : JpaRepository<Payment?, Long?>
