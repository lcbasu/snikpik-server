package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.PaymentServiceConverter
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
        return paymentServiceConverter.getSavedPaymentResponse(payment)
    }

}
