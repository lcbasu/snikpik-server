package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Employee
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
data class EmployeeCompletePaymentDetailsRequest(
    val employeeId: String,
    val forYear: Int,
    val forMonth: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MonthPaymentResponse(
    val yearNumber: Int,
    val monthNumber: Int,
    val amount: Long,
    val monthlyPaymentType: MonthlyPaymentType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentReportResponse(
    val employee: SavedEmployeeResponse,
    val currentYearNumber: Int,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthMonthNumber: Int,
    val prevMonthYearNumber: Int,
    val prevMonthClosing: Long,
    val monthlyPayments: List<MonthPaymentResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CompanyPaymentReportResponse(
    val company: SavedCompanyResponse,
    val currentYearNumber: Int,
    val currentMonthNumber: Int,
    val currentMonthSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthMonthNumber: Int,
    val prevMonthYearNumber: Int,
    val prevMonthClosing: Long,
    val monthlyPayments: List<MonthPaymentResponse>,
    val employeePayments: List<EmployeePaymentReportResponse>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeePaymentDetailsResponse(
    val employee: SavedEmployeeResponse,
    val currentYearNumber: Int,
    val currentMonthNumber: Int,
    val currentMonthWorkingStartDate: Long,
    val currentMonthWorkingEndDate: Long,
    val currentMonthWorkingDays: Int,
    val currentMonthActualSalary: Long,
    val currentMonthPaidSalary: Long,
    val currentMonthPayments: Long,
    val monthlyPayments: List<MonthPaymentResponse>
)

data class DailyPaymentResponse(
    val employeeId: String,
    val forDate: String,
    val dateNumber: Int,
    val dateText: String,
    val salaryAmount: Long,
    val paymentsAmount: Long,
    val payments: List<PaymentMiniDetailsResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentMiniDetailsResponse(
    val employeeId: String,
    val companyId: String,
    val serverId: String,
    val forDate: String,
    val paymentType: PaymentType,
    val amountInPaisa: Long,
    val multiplierUsed: Int,
    val addedAt: Long,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeCompletePaymentDetailsResponse(
    val employee: SavedEmployeeResponse,
    val currentYearNumber: Int,
    val currentMonthNumber: Int,
    val currentMonthWorkingStartDate: Long,
    val currentMonthWorkingEndDate: Long,
    val currentMonthWorkingDays: Int,
    val currentMonthActualSalary: Long,
    val currentMonthPaidSalary: Long,
    val currentMonthPayments: Long,
    val prevMonthMonthNumber: Int,
    val prevMonthYearNumber: Int,
    val prevMonthClosing: Long,
    val dailyPayments: List<DailyPaymentResponse>
)
