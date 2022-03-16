package com.server.shop.dao

import com.server.shop.entities.AddressV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressV3Repository : JpaRepository<AddressV3?, String?>
