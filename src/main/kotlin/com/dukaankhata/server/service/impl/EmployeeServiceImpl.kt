package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.EmployeeRepository
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.service.EmployeeService
import com.dukaankhata.server.service.converter.EmployeeServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmployeeServiceImpl : EmployeeService() {

    @Autowired
    var employeeRepository: EmployeeRepository? = null

    @Autowired
    val authUtils: AuthUtils? = null

    @Autowired
    val companyUtils: CompanyUtils? = null

    @Autowired
    val companyServiceConverter: EmployeeServiceConverter? = null

    override fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? {
        val createdByUser = authUtils?.getRequestUserEntity()
        val company = companyUtils?.getCompany(saveEmployeeRequest.companyId)
        if (createdByUser == null || company == null) {
            error("User and Company are required to add an employee");
        }
        val createdForUser = authUtils?.getOrCreateUserByPhoneNumber(saveEmployeeRequest.phoneNumber)
            ?: error("User should be created for new employee without any user account")
        val employee = employeeRepository?.let {
            val newEmployee = Employee()
            newEmployee.name = saveEmployeeRequest.name
            newEmployee.balanceInPaisaTillNow = saveEmployeeRequest.balanceInPaisaTillNow
            newEmployee.openingBalanceInPaisa = saveEmployeeRequest.openingBalanceInPaisa
            newEmployee.phoneNumber = saveEmployeeRequest.phoneNumber
            newEmployee.salaryAmountInPaisa = saveEmployeeRequest.salaryAmountInPaisa
            newEmployee.salaryType = saveEmployeeRequest.salaryType
            newEmployee.openingBalanceType = saveEmployeeRequest.openingBalanceType ?: OpeningBalanceType.NONE
            newEmployee.joinedAt = LocalDateTime.now()
            newEmployee.company = company
            newEmployee.createdByUser = createdByUser
            newEmployee.createdForUser = createdForUser
            it.save(newEmployee)
        }

        return companyServiceConverter?.getSavedEmployeeResponse(employee);
    }

    override fun getEmployee(): SavedEmployeeResponse? {
        return super.getEmployee()
    }
}
