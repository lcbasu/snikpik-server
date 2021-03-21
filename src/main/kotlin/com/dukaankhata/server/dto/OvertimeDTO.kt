package com.dukaankhata.server.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveOvertimeRequest(
    val employeeId: Long,
    val companyId: Long,
    val forDate: String,
    val hourlyOvertimeWageInPaisa: Long,
    val totalOvertimeMinutes: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedOvertimeResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val employee: SavedEmployeeResponse,
    val payment: SavedPaymentResponse,
    val forDate: String,
    val hourlyOvertimeWageInPaisa: Long,
    val totalOvertimeMinutes: Int,
    val totalOvertimeAmountInPaisa: Long,
    val addedAt: Long,
)

