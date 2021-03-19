package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface EmployeeRepository : JpaRepository<Employee?, Long?> {

    @Query(value ="SELECT * FROM employee WHERE joined_at < :forDate and company_id = :companyId", nativeQuery = true)
    fun getEmployeesForDate(
        @Param("companyId") companyId: Long,
        @Param("forDate") forDate: LocalDateTime
    ): List<Employee>

    fun findByCompany(company: Company): List<Employee>
}
