package com.dukaankhata.server.service

import com.dukaankhata.server.dto.CompanyEmployeesResponse
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse

abstract class EmployeeService {
    abstract fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse?
    abstract fun getCompanyEmployees(companyServerId: Long): CompanyEmployeesResponse?
}
