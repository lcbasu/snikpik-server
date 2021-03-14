package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CompanyRepository : JpaRepository<Company?, Long?>
