package com.server.dk.dao

import com.server.dk.entities.Collection
import com.server.dk.entities.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CollectionRepository : JpaRepository<Collection?, String?> {
    fun findAllByCompany(company: Company): List<Collection>
}
