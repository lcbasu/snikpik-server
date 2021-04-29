package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address?, String?>
