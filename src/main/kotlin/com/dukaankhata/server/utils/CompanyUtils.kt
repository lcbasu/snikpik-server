package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.entities.Company
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyUtils {

    @Autowired
    private lateinit var companyRepository: CompanyRepository

    fun getCompany(companyId: Long): Company? =
        try {
            companyRepository.findById(companyId).get()
        } catch (e: Exception) {
            null
        }
}
