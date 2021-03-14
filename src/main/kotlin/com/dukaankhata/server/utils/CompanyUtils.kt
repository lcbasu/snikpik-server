package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CompanyRepository
import com.dukaankhata.server.entities.Company
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyUtils {

    @Autowired
    var companyRepository: CompanyRepository? = null

    fun getCompany(companyId: Long): Company? {
        val company = companyRepository?.findById(companyId)
        return company?.get()
    }
}
