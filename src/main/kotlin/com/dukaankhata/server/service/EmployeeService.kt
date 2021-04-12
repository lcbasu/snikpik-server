package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class EmployeeService {
    abstract fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse?
    abstract fun getCompanyEmployees(companyServerId: Long): CompanyEmployeesResponse?
    abstract fun removeEmployee(removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse?
    abstract fun updateSalary(employeeId: Long, forDate: String): SavedEmployeeResponse?
    abstract fun getSalarySlip(employeeId: Long, startDate: String, endDate: String): SalarySlipResponse?
}
