package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.PaymentSummaryRequest
import com.dukaankhata.server.dto.PaymentSummaryResponse
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.PaymentServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CompanyUtils
import com.dukaankhata.server.utils.EmployeeUtils
import com.dukaankhata.server.utils.PaymentUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Service
class PaymentServiceImpl : PaymentService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var paymentServiceConverter: PaymentServiceConverter

    override fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = savePaymentRequest.employeeId,
            companyId = savePaymentRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val payment =  paymentUtils.savePayment(
            addedBy = requestContext.user,
            company = requestContext.company!!,
            employee = requestContext.employee!!,
            savePaymentRequest = savePaymentRequest
        )

        // Update the employee payment
        employeeUtils.updateEmployee(payment)

        // Update the company payment
        companyUtils.updateCompany(payment)
        return paymentServiceConverter.getSavedPaymentResponse(payment)
    }

    override fun getPaymentSummary(paymentSummaryRequest: PaymentSummaryRequest): PaymentSummaryResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = paymentSummaryRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        // Choosing a random date in middle to select correct start and end month
        val startDate = LocalDateTime.of(paymentSummaryRequest.forYear, paymentSummaryRequest.forMonth, 20, 0, 0, 0, 0)
        val startTime = startDate.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay().minusMonths(2) // get data from past 2 months
        val endTime = startDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)

        val payments = paymentUtils.getAllPaymentsBetweenGivenTimes(
            companyId = requestContext.company!!.id,
            startTime = startTime,
            endTime = endTime
        )
        return paymentServiceConverter.getPaymentSummary(requestContext.company, payments)
    }

}
