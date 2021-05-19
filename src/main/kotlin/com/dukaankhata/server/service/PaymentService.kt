package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class PaymentService {
    abstract fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse?
    abstract fun getCompanyPaymentReport(companyPaymentReportRequest: CompanyPaymentReportRequest): CompanyPaymentReportResponse?
    abstract fun getEmployeePaymentDetails(employeePaymentDetailsRequest: EmployeePaymentDetailsRequest): EmployeePaymentDetailsResponse?
    abstract fun getEmployeeCompletePaymentDetails(employeeCompletePaymentDetailsRequest: EmployeeCompletePaymentDetailsRequest): EmployeeCompletePaymentDetailsResponse?
}
