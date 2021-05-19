package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.PaymentServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.PaymentUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl : PaymentService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    @Autowired
    private lateinit var paymentServiceConverter: PaymentServiceConverter

    override fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = savePaymentRequest.employeeId,
            companyId = savePaymentRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        return paymentServiceConverter.getSavedPaymentResponse(paymentUtils.savePaymentAndDependentData(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            forDate = savePaymentRequest.forDate,
            paymentType = savePaymentRequest.paymentType,
            amountInPaisa = savePaymentRequest.amountInPaisa,
            description = savePaymentRequest.description
        ))
    }

    override fun getCompanyPaymentReport(companyPaymentReportRequest: CompanyPaymentReportRequest): CompanyPaymentReportResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = companyPaymentReportRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val company = requestContext.company ?: error("Company is required")

        val monthlyPaymentSummary = paymentUtils.getMonthlyPaymentSummary(
            company = company,
            forYear = companyPaymentReportRequest.forYear,
            forMonth = companyPaymentReportRequest.forMonth
        )
        return paymentUtils.getCompanyPaymentReport(forYear = companyPaymentReportRequest.forYear,
            forMonth = companyPaymentReportRequest.forMonth, requestContext.company, monthlyPaymentSummary)
    }

    override fun getEmployeePaymentDetails(employeePaymentDetailsRequest: EmployeePaymentDetailsRequest): EmployeePaymentDetailsResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeePaymentDetailsRequest.employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee is required")
        val monthlyPaymentSummary = paymentUtils.getMonthlyPaymentSummary(
            employee, forYear = employeePaymentDetailsRequest.forYear, forMonth = employeePaymentDetailsRequest.forMonth
        )
        return paymentUtils.getEmployeePaymentDetails(
            employee,
            forYear = employeePaymentDetailsRequest.forYear,
            forMonth = employeePaymentDetailsRequest.forMonth,
            monthlyPaymentSummary)
    }

    override fun getEmployeeCompletePaymentDetails(employeeCompletePaymentDetailsRequest: EmployeeCompletePaymentDetailsRequest): EmployeeCompletePaymentDetailsResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = employeeCompletePaymentDetailsRequest.employeeId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val employee = requestContext.employee ?: error("Employee is required")
        return paymentUtils.getEmployeeCompletePaymentDetails(
            employee,
            forYear = employeeCompletePaymentDetailsRequest.forYear,
            forMonth = employeeCompletePaymentDetailsRequest.forMonth)
    }

}
