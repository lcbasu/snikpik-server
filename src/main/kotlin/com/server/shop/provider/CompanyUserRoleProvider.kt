package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.CompanyUserRoleRepository
import com.server.shop.entities.CompanyUserRole
import com.server.shop.entities.CompanyUserRoleKey
import com.server.shop.enums.CompanyUserRoleType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompanyUserRoleProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var companyUserRoleRepository: CompanyUserRoleRepository

    fun getBrandUserRoleKey(companyId: String, roleType: CompanyUserRoleType): CompanyUserRoleKey {
        val key = CompanyUserRoleKey()
        key.companyId = companyId
        key.roleType = roleType
        return key
    }

    fun getCompanyUserRole(companyId: String, roleType: CompanyUserRoleType): CompanyUserRole? =
        try {
            val key = getBrandUserRoleKey(companyId = companyId, roleType = roleType)
            companyUserRoleRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun getCompanyUserRoleList(companyId: String): List<CompanyUserRole> =
        try {
            companyUserRoleRepository.findAllByCompanyId(companyId)
        } catch (e: Exception) {
            emptyList()
        }
}
