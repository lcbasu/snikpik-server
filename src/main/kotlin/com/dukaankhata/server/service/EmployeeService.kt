package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class EmployeeService {
    abstract fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse?
    abstract fun getCompanyEmployees(companyServerId: String): CompanyEmployeesResponse?
    abstract fun removeEmployee(removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse?
    abstract fun updateSalary(employeeId: String, forDate: String): SavedEmployeeResponse?
    abstract fun getSalarySlip(employeeId: String, startDate: String, endDate: String): SalarySlipResponse?
    abstract fun updateEmployeeJoiningDate(updateEmployeeJoiningDateRequest: UpdateEmployeeJoiningDateRequest): SavedEmployeeResponse?
}
