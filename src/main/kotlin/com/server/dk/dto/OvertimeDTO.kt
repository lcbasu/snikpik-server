package com.server.dk.dto

import com.server.dk.entities.Overtime
import com.server.dk.entities.Payment
import com.server.dk.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveOvertimeRequest(
    val employeeId: String,
    val companyId: String,
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

fun Overtime.toSavedOvertimeResponse(payment: Payment): SavedOvertimeResponse {
    return SavedOvertimeResponse(
        serverId = id,
        company = company!!.toSavedCompanyResponse(),
        employee = employee!!.toSavedEmployeeResponse(),
        payment = payment.toSavedPaymentResponse(),
        forDate = forDate,
        hourlyOvertimeWageInPaisa = hourlyOvertimeWageInPaisa,
        totalOvertimeMinutes = totalOvertimeMinutes,
        totalOvertimeAmountInPaisa = totalOvertimeAmountInPaisa,
        addedAt = DateUtils.getEpoch(addedAt))
}
