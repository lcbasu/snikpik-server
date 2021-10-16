package com.server.dk.service

import com.server.dk.dto.*

abstract class PaymentService {
    abstract fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse?
    abstract fun getCompanyPaymentReport(companyPaymentReportRequest: CompanyPaymentReportRequest): CompanyPaymentReportResponse?
    abstract fun getEmployeePaymentDetails(employeePaymentDetailsRequest: EmployeePaymentDetailsRequest): EmployeePaymentDetailsResponse?
    abstract fun getEmployeeCompletePaymentDetails(employeeCompletePaymentDetailsRequest: EmployeeCompletePaymentDetailsRequest): EmployeeCompletePaymentDetailsResponse?
}
