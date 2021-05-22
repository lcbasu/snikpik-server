package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
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
    fun getCompanyEmployees(@PathVariable companyServerId: String): CompanyEmployeesResponse? {
        return employeeService.getCompanyEmployees(companyServerId)
    }

    @RequestMapping(value = ["/remove"], method = [RequestMethod.POST])
    fun removeEmployee(@RequestBody removeEmployeeRequest: RemoveEmployeeRequest): SavedEmployeeResponse? {
        return employeeService.removeEmployee(removeEmployeeRequest)
    }

    @RequestMapping(value = ["/updateSalary/{employeeId}/{forDate}"], method = [RequestMethod.POST])
    fun updateSalary(@PathVariable employeeId: String, @PathVariable forDate: String): SavedEmployeeResponse? {
        return employeeService.updateSalary(employeeId, forDate)
    }

    @RequestMapping(value = ["/updateEmployeeJoiningDate"], method = [RequestMethod.POST])
    fun updateEmployeeJoiningDate(@RequestBody updateEmployeeJoiningDateRequest: UpdateEmployeeJoiningDateRequest): SavedEmployeeResponse? {
        return employeeService.updateEmployeeJoiningDate(updateEmployeeJoiningDateRequest)
    }

    @RequestMapping(value = ["/getSalarySlip/{employeeId}/{startDate}/{endDate}"], method = [RequestMethod.GET])
    fun getSalarySlip(@PathVariable employeeId: String, @PathVariable startDate: String, @PathVariable endDate: String): SalarySlipResponse? {
        return employeeService.getSalarySlip(employeeId, startDate, endDate)
    }

}
