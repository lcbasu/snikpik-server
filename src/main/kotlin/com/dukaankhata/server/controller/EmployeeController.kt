package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse
import com.dukaankhata.server.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("employee")
class EmployeeController {
    @Autowired
    var employeeService: EmployeeService? = null

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        return employeeService?.saveEmployee(saveEmployeeRequest)
    }
}
