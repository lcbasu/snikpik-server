package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.MonthlyPaymentType
import com.dukaankhata.server.enums.PaymentType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavePaymentRequest(
    val employeeId: String,
    val companyId: String,
    val forDate: String,
    val paymentType: PaymentType,
    val amountInPaisa: Long,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPaymentResponse(
    val employee: SavedEmployeeResponse,
    val company: SavedCompanyResponse,
    val serverId: String,
    val forDate: String,
    val paymentType: PaymentType,
    val amountInPaisa: Long,
    val multiplierUsed: Int,
    val addedAt: Long,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentSummaryRequest(
    val companyId: String,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MonthPaymentResponse(
    val monthNumber: Int,
    val amount: Long,
    val monthlyPaymentType: MonthlyPaymentType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentSummaryResponse(
    val employee: SavedEmployeeResponse,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthNumber: Int,
    val prevMonthClosing: Long,
    // For current and last 2 months
    // Only for the current employee
    val monthlyPayments: List<MonthPaymentResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentSummaryResponse(
    val company: SavedCompanyResponse,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthNumber: Int,
    val prevMonthClosing: Long,
    val monthlyPayments: List<MonthPaymentResponse>,
    val employeePayments: List<EmployeePaymentSummaryResponse>,
)
