package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Holiday
import com.dukaankhata.server.enums.HolidayType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveHolidayRequest(
    val employeeId: String,
    val companyId: String,
    val forDate: String,
    val holidayType: HolidayType
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedHolidayResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val employee: SavedEmployeeResponse,
    val forDate: String,
    val holidayType: HolidayType,
)

fun Holiday.toSavedHolidayResponse(): SavedHolidayResponse {
    return SavedHolidayResponse(
        serverId = id?.companyId.toString() + "__" + id?.employeeId.toString() + "__" + id?.forDate,
        company = company!!.toSavedCompanyResponse(),
        employee = employee!!.toSavedEmployeeResponse(),
        forDate = id?.forDate ?: "",
        holidayType = holidayType)
}
