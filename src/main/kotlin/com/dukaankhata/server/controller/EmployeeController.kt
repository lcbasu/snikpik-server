package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.CompanyEmployeesResponse
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse
import com.dukaankhata.server.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("employee")
class EmployeeController {
    @Autowired
    var employeeService: EmployeeService? = null

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        return employeeService?.saveEmployee(saveEmployeeRequest)
    }

    @RequestMapping(value = ["/getCompanyEmployees/{companyServerId}"], method = [RequestMethod.GET])
    fun getCompanyEmployees(@PathVariable companyServerId: Long): CompanyEmployeesResponse? {
        return employeeService?.getCompanyEmployees(companyServerId)
    }
}
