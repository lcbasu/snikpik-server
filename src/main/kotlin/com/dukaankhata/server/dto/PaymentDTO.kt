package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.PaymentType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavePaymentRequest(
    val employeeId: Long,
    val companyId: Long,
    val forDate: String,
    val paymentType: PaymentType,
    val amountInPaisa: Long,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPaymentResponse(
    val employee: SavedEmployeeResponse,
    val company: SavedCompanyResponse,
    val serverId: Long,
    val forDate: String,
    val paymentType: PaymentType,
    val amountInPaisa: Long,
    val multiplierUsed: Int,
    val addedAt: Long,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentSummaryRequest(
    val companyId: Long,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MonthPayment(
    val monthNumber: Int,
    val amount: Long,
    //val payments: List<SavedPaymentResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentSummaryResponse(
    val employee: SavedEmployeeResponse,
    // For current and last 2 months
    // Only for the current employee
    val monthlyPayments: List<MonthPayment>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentSummaryResponse(
    val company: SavedCompanyResponse,
    // For current and last 2 months for the entire company
    val monthlyPayments: List<MonthPayment>,
    val employeePayments: List<EmployeePaymentSummaryResponse>,
)
