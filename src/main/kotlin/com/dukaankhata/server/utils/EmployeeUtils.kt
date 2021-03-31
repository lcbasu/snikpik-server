package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.EmployeeRepository
import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.service.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EmployeeUtils {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var paymentService: PaymentService

    fun getEmployee(employeeId: Long): Employee? =
        try {
            employeeRepository.findById(employeeId).get()
        } catch (e: Exception) {
            null
        }

    fun getEmployeesForDate(companyId: Long, forDate: String): List<Employee> =
        try {
            // We are adding one day to check for anyone who has been added as an employee today
            // and hence the attendance count has to consider that
            // There could be scenarios where we added the employees at 9:00 am on March 21, 2021
            // But the datetime form DateUtils.parseStandardDate(forDate) will be March 21, 2021, 00am
            // So the new employees will not be picked up
            employeeRepository.getEmployeesForDate(companyId, DateUtils.parseStandardDate(forDate).plusDays(1))
        } catch (e: Exception) {
            emptyList()
        }

    fun saveEmployee(createdByUser: User, createdForUser: User, company: Company, saveEmployeeRequest: SaveEmployeeRequest) : Employee {
        val newEmployee = Employee()
        newEmployee.name = saveEmployeeRequest.name
        newEmployee.balanceInPaisaTillNow = saveEmployeeRequest.balanceInPaisaTillNow
        newEmployee.openingBalanceInPaisa = saveEmployeeRequest.openingBalanceInPaisa
        newEmployee.phoneNumber = saveEmployeeRequest.phoneNumber
        newEmployee.salaryAmountInPaisa = saveEmployeeRequest.salaryAmountInPaisa
        newEmployee.salaryType = saveEmployeeRequest.salaryType
        newEmployee.salaryCycle = saveEmployeeRequest.salaryCycle
        newEmployee.openingBalanceType = saveEmployeeRequest.openingBalanceType ?: OpeningBalanceType.NONE
        newEmployee.joinedAt = DateUtils.dateTimeNow()
        newEmployee.company = company
        newEmployee.createdByUser = createdByUser
        newEmployee.createdForUser = createdForUser

        val employee = employeeRepository.save(newEmployee)

        if (employee.openingBalanceInPaisa != 0L && employee.openingBalanceType != OpeningBalanceType.NONE) {
            val paymentType = if (employee.openingBalanceType == OpeningBalanceType.ADVANCE) PaymentType.PAYMENT_OPENING_BALANCE_ADVANCE else PaymentType.PAYMENT_OPENING_BALANCE_PENDING
            val savedPaymentResponse = paymentService.savePayment(SavePaymentRequest(
                employeeId = employee.id,
                companyId = employee.company?.id ?: -1,
                forDate = DateUtils.toStringDate(DateUtils.dateTimeNow()),
                paymentType = paymentType,
                amountInPaisa = employee.openingBalanceInPaisa,
                description = "Added by system for opening balance",
            )) ?: error("Payment was not saved for Employee with opening balance")
        }

        return employee
    }

    fun updateEmployee(payment: Payment) : Employee {
        val employee = payment.employee ?: error("Payment should always have an employee object")
        employee.balanceInPaisaTillNow = employee.balanceInPaisaTillNow + (payment.multiplierUsed * payment.amountInPaisa)
        employee.lastModifiedAt = DateUtils.dateTimeNow()
        return employeeRepository.save(employee)
    }
}
