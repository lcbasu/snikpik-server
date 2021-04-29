package com.dukaankhata.server.dto

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

