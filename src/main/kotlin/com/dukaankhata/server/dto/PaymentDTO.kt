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
data class CompanyPaymentReportRequest(
    val companyId: String,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentDetailsRequest(
    val employeeId: String,
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
data class EmployeePaymentReportResponse(
    val employee: SavedEmployeeResponse,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthNumber: Int,
    val prevMonthClosing: Long,
    val monthlyPayments: List<MonthPaymentResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CompanyPaymentReportResponse(
    val company: SavedCompanyResponse,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthNumber: Int,
    val prevMonthClosing: Long,
    val monthlyPayments: List<MonthPaymentResponse>,
    val employeePayments: List<EmployeePaymentReportResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentDetailsResponse(
    val employee: SavedEmployeeResponse,
    val currentMonthNumber: Int,
    val currentMonthWorkingStartDate: Long,
    val currentMonthWorkingEndDate: Long,
    val currentMonthWorkingDays: Int,
    val currentMonthActualSalary: Long,
    val currentMonthPaidSalary: Long,
    val currentMonthPayments: Long,
    val monthlyPayments: List<MonthPaymentResponse>
)
