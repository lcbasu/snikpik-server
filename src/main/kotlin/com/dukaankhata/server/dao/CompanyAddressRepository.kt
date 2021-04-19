package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.CompanyAddress
import com.dukaankhata.server.entities.CompanyAddressKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyAddressRepository : JpaRepository<CompanyAddress?, CompanyAddressKey?>
