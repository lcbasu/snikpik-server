package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedHolidayResponse
import com.dukaankhata.server.entities.Holiday
import com.dukaankhata.server.entities.HolidayKey
import com.dukaankhata.server.enums.HolidayType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HolidayServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    private fun getServerId(holidayKey: HolidayKey?): String {
        return holidayKey?.companyId.toString() + "__" + holidayKey?.employeeId.toString() + "__" + holidayKey?.forDate;
    };

    fun getSavedHolidayResponse(holiday: Holiday?): SavedHolidayResponse {
        return SavedHolidayResponse(
            serverId = getServerId(holiday?.id),
            company = companyServiceConverter.getSavedCompanyResponse(holiday?.company),
            employee = employeeServiceConverter.getSavedEmployeeResponse(holiday?.employee),
            forDate = holiday?.id?.forDate ?: "",
            holidayType = holiday?.holidayType ?: HolidayType.NONE)
    }
}
