package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedHolidayResponse
import com.dukaankhata.server.entities.Holiday
import com.dukaankhata.server.entities.HolidayKey
import com.dukaankhata.server.enums.HolidayType
import org.springframework.stereotype.Component

@Component
class HolidayServiceConverter {


    private fun getServerId(holidayKey: HolidayKey?): String {
        return holidayKey?.companyId.toString() + "__" + holidayKey?.employeeId.toString() + "__" + holidayKey?.forDate;
    };

    fun getSavedHolidayResponse(holiday: Holiday?): SavedHolidayResponse {
        return SavedHolidayResponse(
            serverId = getServerId(holiday?.id),
            employeeId = holiday?.employee?.id ?: -1,
            companyId = holiday?.company?.id ?: -1,
            forDate = holiday?.id?.forDate ?: "",
            holidayType = holiday?.holidayType ?: HolidayType.NONE,
            addedByUserId = holiday?.addedBy?.id ?: "")
    }
}
