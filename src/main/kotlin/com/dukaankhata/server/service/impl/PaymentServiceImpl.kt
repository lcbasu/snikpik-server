package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.PaymentSummaryRequest
import com.dukaankhata.server.dto.PaymentSummaryResponse
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.PaymentServiceConverter
import com.dukaankhata.server.utils.AuthUtils
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

    override fun getPaymentSummary(paymentSummaryRequest: PaymentSummaryRequest): PaymentSummaryResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = paymentSummaryRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )

        val company = requestContext.company ?: error("Company is required")

        val monthlyPaymentSummary = paymentUtils.getMonthlyPaymentSummary(
            company = company,
            forYear = paymentSummaryRequest.forYear,
            forMonth = paymentSummaryRequest.forMonth
        )
        return paymentServiceConverter.getPaymentSummary(forYear = paymentSummaryRequest.forYear,
            forMonth = paymentSummaryRequest.forMonth, requestContext.company, monthlyPaymentSummary)
    }

}
