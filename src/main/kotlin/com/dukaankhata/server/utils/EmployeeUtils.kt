package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.EmployeeRepository
import com.dukaankhata.server.entities.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmployeeUtils {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    fun getEmployee(employeeId: Long): Employee? =
        try {
            employeeRepository.findById(employeeId).get()
        } catch (e: Exception) {
            null
        }

    fun getEmployeesForDate(companyId: Long, forDate: String): List<Employee> =
        try {
            // We are adding one day to check for anyone who has been added as an employee today
            // and hence the attendance count has to consider that
            // There could be scenarios where we added the employees at 9:00 am on March 21, 2021
            // But the datetime form DateUtils.parseStandardDate(forDate) will be March 21, 2021, 00am
            // So the new employees will not be picked up
            employeeRepository.getEmployeesForDate(companyId, DateUtils.parseStandardDate(forDate).plusDays(1))
        } catch (e: Exception) {
            emptyList()
        }
}
