package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.CompanyEmployeesResponse
import com.dukaankhata.server.dto.RemoveEmployeeRequest
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse
import com.dukaankhata.server.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("employee")
class EmployeeController {
    @Autowired
    private lateinit var employeeService: EmployeeService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveEmployee(@RequestBody saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        return employeeService.saveEmployee(saveEmployeeRequest)
    }

    @RequestMapping(value = ["/getCompanyEmployees/{companyServerId}"], method = [RequestMethod.GET])
    fun getCompanyEmployees(@PathVariable companyServerId: Long): CompanyEmployeesResponse? {
        return employeeService.getCompanyEmployees(companyServerId)
    }

    @RequestMapping(value = ["/remove"], method = [RequestMethod.POST])
    fun removeEmployee(@RequestBody removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse? {
        return employeeService.removeEmployee(removeEmployeeRequest)
    }

    @RequestMapping(value = ["/updateSalary/{employeeId}/{forDate}"], method = [RequestMethod.POST])
    fun updateSalary(@PathVariable employeeId: Long, @PathVariable forDate: String): SavedEmployeeResponse? {
        return employeeService.updateSalary(employeeId, forDate)
    }

}
