package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.LateFine
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveLateFineRequest(
    val employeeId: String,
    val companyId: String,
    val forDate: String,
    val totalLateFineMinutes: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedLateFineResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val employee: SavedEmployeeResponse,
    val payment: SavedPaymentResponse,
    val forDate: String,
    val hourlyLateFineWageInPaisa: Long, // Calculated using salary and the working hours
    val totalLateFineMinutes: Int,
    val totalLateFineAmountInPaisa: Long,
    val addedAt: Long,
)

fun LateFine.toSavedLateFineResponse(payment: Payment): SavedLateFineResponse {
    return SavedLateFineResponse(
        serverId = id,
        company = company!!.toSavedCompanyResponse(),
        employee = employee!!.toSavedEmployeeResponse(),
        payment = payment.toSavedPaymentResponse(),
        forDate = forDate ?: "",
        hourlyLateFineWageInPaisa = hourlyLateFineWageInPaisa ?: 0,
        totalLateFineMinutes = totalLateFineMinutes ?: 0,
        totalLateFineAmountInPaisa = totalLateFineAmountInPaisa ?: 0,
        addedAt = DateUtils.getEpoch(addedAt))
}
