package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.ProductOrder
import com.server.common.entities.User
import com.server.dk.enums.ProductOrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderRepository : JpaRepository<ProductOrder?, String?> {
    fun findAllByCompanyAndAddedBy(company: Company, user: User): List<ProductOrder>
    fun findAllByCompanyAndAddedByAndOrderStatus(company: Company, user: User, orderStatus: ProductOrderStatus): List<ProductOrder>
    fun findAllByAddedBy(user: User): List<ProductOrder>
    fun findAllByCompany(company: Company): List<ProductOrder>
    fun findAllByAddedByAndOrderStatus(user: User, orderStatus: ProductOrderStatus): List<ProductOrder>
    fun findAllByCompanyAndOrderStatusNotIn(company: Company, orderStatusNotIn: Set<ProductOrderStatus>): List<ProductOrder>
}
