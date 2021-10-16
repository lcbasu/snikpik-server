package com.server.dk.service.impl

import com.server.dk.dto.SaveHolidayRequest
import com.server.dk.dto.SavedHolidayResponse
import com.server.dk.dto.toSavedHolidayResponse
import com.server.dk.provider.*
import com.server.dk.service.HolidayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HolidayServiceImpl : HolidayService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var holidayProvider: HolidayProvider

    @Autowired
    private lateinit var employeeProvider: EmployeeProvider

    @Autowired
    private lateinit var userRoleProvider: UserRoleProvider

    override fun saveHoliday(saveHolidayRequest: SaveHolidayRequest): SavedHolidayResponse? {
        val addedByUser = authProvider.getRequestUserEntity()
        val company = companyProvider.getCompany(saveHolidayRequest.companyId)
        val employee = employeeProvider.getEmployee(saveHolidayRequest.employeeId)
        if (addedByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleProvider.getUserRolesForUserAndCompany(
            user = addedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can mark the attendance");
        }

        val holiday =  holidayProvider.saveOrUpdateHoliday(
            addedBy = addedByUser,
            company = company,
            employee = employee,
            forDate = saveHolidayRequest.forDate,
            holidayType = saveHolidayRequest.holidayType
        )
        return holiday.toSavedHolidayResponse()
    }

}
