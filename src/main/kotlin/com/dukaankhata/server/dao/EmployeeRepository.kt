package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface EmployeeRepository : JpaRepository<Employee?, Long?>
