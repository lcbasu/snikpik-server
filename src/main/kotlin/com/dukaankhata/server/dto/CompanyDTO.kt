package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.SalaryPaymentSchedule
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveCompanyRequest(
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedCompanyResponse(
    val serverId: String,
    val name: String,
    val location: String,
    val salaryPaymentSchedule: SalaryPaymentSchedule,
    val workingMinutes: Int,
    val userId: String,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class UserCompaniesResponse(
    val companies: List<SavedCompanyResponse>,
)
