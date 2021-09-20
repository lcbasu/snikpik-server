package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.PaymentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl : PaymentService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var paymentProvider: PaymentProvider

    override fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = savePaymentRequest.employeeId,
            companyServerIdOrUsername = savePaymentRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        return paymentProvider.savePaymentAndDependentData(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = savePaymentRequest.forDate,
            paymentType = savePaymentRequest.paymentType,
            amountInPaisa = savePaymentRequest.amountInPaisa,
            description = savePaymentRequest.description
        ).toSavedPaymentResponse()
    }

    override fun getCompanyPaymentReport(companyPaymentReportRequest: CompanyPaymentReportRequest): CompanyPaymentReportResponse? {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyPaymentReportRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )

        val company = requestContext.company ?: error("Company is required")

        val monthlyPaymentSummary = paymentProvider.getMonthlyPaymentSummary(
            company = company,
            forYear = companyPaymentReportRequest.forYear,
            forMonth = companyPaymentReportRequest.forMonth
        )
        return paymentProvider.getCompanyPaymentReport(forYear = companyPaymentReportRequest.forYear,
            forMonth = companyPaymentReportRequest.forMonth, requestContext.company, monthlyPaymentSummary)
    }

    override fun getEmployeePaymentDetails(employeePaymentDetailsRequest: EmployeePaymentDetailsRequest): EmployeePaymentDetailsResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = employeePaymentDetailsRequest.employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee is required")
        val monthlyPaymentSummary = paymentProvider.getMonthlyPaymentSummary(
            employee, forYear = employeePaymentDetailsRequest.forYear, forMonth = employeePaymentDetailsRequest.forMonth
        )
        return paymentProvider.getEmployeePaymentDetails(
            employee,
            forYear = employeePaymentDetailsRequest.forYear,
            forMonth = employeePaymentDetailsRequest.forMonth,
            monthlyPaymentSummary)
    }

    override fun getEmployeeCompletePaymentDetails(employeeCompletePaymentDetailsRequest: EmployeeCompletePaymentDetailsRequest): EmployeeCompletePaymentDetailsResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = employeeCompletePaymentDetailsRequest.employeeId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee is required")
        return paymentProvider.getEmployeeCompletePaymentDetails(
            employee,
            forYear = employeeCompletePaymentDetailsRequest.forYear,
            forMonth = employeeCompletePaymentDetailsRequest.forMonth)
    }

}
