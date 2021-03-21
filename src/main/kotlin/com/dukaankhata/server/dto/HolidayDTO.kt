package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.HolidayType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveHolidayRequest(
    val employeeId: Long,
    val companyId: Long,
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

