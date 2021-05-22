package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveHolidayRequest
import com.dukaankhata.server.dto.SavedHolidayResponse
import com.dukaankhata.server.dto.toSavedHolidayResponse
import com.dukaankhata.server.service.HolidayService
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HolidayServiceImpl : HolidayService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var holidayUtils: HolidayUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    override fun saveHoliday(saveHolidayRequest: SaveHolidayRequest): SavedHolidayResponse? {
        val addedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveHolidayRequest.companyId)
        val employee = employeeUtils.getEmployee(saveHolidayRequest.employeeId)
        if (addedByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = addedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can mark the attendance");
        }

        val holiday =  holidayUtils.saveOrUpdateHoliday(
            addedBy = addedByUser,
            company = company,
            employee = employee,
            forDate = saveHolidayRequest.forDate,
            holidayType = saveHolidayRequest.holidayType
        )
        return holiday.toSavedHolidayResponse()
    }

}
