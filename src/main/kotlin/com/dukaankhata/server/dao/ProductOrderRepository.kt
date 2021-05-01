package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ProductOrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderRepository : JpaRepository<ProductOrder?, String?> {
    fun findAllByCompanyAndAddedByAndOrderStatus(company: Company, user: User, orderStatus: ProductOrderStatus): List<ProductOrder>
    fun findAllByAddedByAndOrderStatus(user: User, orderStatus: ProductOrderStatus): List<ProductOrder>
}
