package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.PaymentRepository
import com.dukaankhata.server.dto.PaymentSummaryRequest
import com.dukaankhata.server.dto.PaymentSummaryResponse
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.PaymentServiceConverter
import com.dukaankhata.server.utils.*
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

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    override fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse? {
        val addedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(savePaymentRequest.companyId)
        val employee = employeeUtils.getEmployee(savePaymentRequest.employeeId)
        if (addedByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = addedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can add payment");
        }

        // TODO: Employee 1 can not add payment for Employee 2 unless

        val payment =  paymentUtils.savePayment(
            addedBy = addedByUser,
            company = company,
            employee = employee,
            savePaymentRequest = savePaymentRequest
        )

        // Update the employee payment
        employeeUtils.updateEmployee(payment)

        // Update the company payment
        companyUtils.updateCompany(payment)
        return paymentServiceConverter.getSavedPaymentResponse(payment)
    }

    override fun getPaymentSummary(paymentSummaryRequest: PaymentSummaryRequest): PaymentSummaryResponse? {
        val requestedByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(paymentSummaryRequest.companyId)
        if (requestedByUser == null || company == null) {
            error("User, and Company are required to get payment summary");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = requestedByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can view payment");
        }

        // Choosing a random date in middle to select correct start and end month
        val startDate = LocalDateTime.of(paymentSummaryRequest.forYear, paymentSummaryRequest.forMonth, 20, 0, 0, 0, 0)
        val startTime = startDate.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay().minusMonths(2) // get data from past 2 months
        val endTime = startDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(LocalTime.MAX)

        val payments = paymentRepository.getAllPaymentsBetweenGivenTimes(
            companyId = company.id,
            startTime = startTime,
            endTime = endTime
        )
        return paymentServiceConverter.getPaymentSummary(company, payments)
    }

}
