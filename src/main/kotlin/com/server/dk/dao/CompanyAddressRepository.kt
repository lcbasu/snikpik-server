package com.server.dk.dao

import com.server.dk.entities.CompanyAddress
import com.server.dk.entities.CompanyAddressKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyAddressRepository : JpaRepository<CompanyAddress?, CompanyAddressKey?>
