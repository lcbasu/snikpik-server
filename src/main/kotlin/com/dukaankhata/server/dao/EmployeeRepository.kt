package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface EmployeeRepository : JpaRepository<Employee?, String?> {

    fun findByCompany(company: Company): List<Employee>

    @Query(value ="SELECT * FROM employee WHERE joined_at <= :joinedBeforeDateTime and company_id = :companyId", nativeQuery = true)
    fun getEmployees(
        @Param("companyId") companyId: String,
        @Param("joinedBeforeDateTime") joinedBeforeDateTime: LocalDateTime,
    ): List<Employee>
}
