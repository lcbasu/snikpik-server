package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.LateFineRepository
import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.LateFine
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.LateFineServiceConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LateFineUtils {

    @Autowired
    private lateinit var lateFineRepository: LateFineRepository

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var lateFineServiceConverter: LateFineServiceConverter

    fun getLateFine(lateFineId: Long): LateFine? =
        try {
            lateFineRepository.findById(lateFineId).get()
        } catch (e: Exception) {
            null
        }

    fun saveLateFine(addedBy: User, company: Company, employee: Employee, forDate: String, saveLateFineRequest: SaveLateFineRequest) : SavedLateFineResponse {

        val hourlyLateFineWageInPaisa = 5L // TODO: Update based on salary

        val lateFine = LateFine()
        lateFine.company = company
        lateFine.employee = employee
        lateFine.addedBy = addedBy
        lateFine.forDate = forDate
        lateFine.hourlyLateFineWageInPaisa = hourlyLateFineWageInPaisa
        lateFine.totalLateFineMinutes = saveLateFineRequest.totalLateFineMinutes
        lateFine.totalLateFineAmountInPaisa = hourlyLateFineWageInPaisa * (saveLateFineRequest.totalLateFineMinutes / 60)
        val savedLateFine = lateFineRepository.save(lateFine)


        // Save Payment for the employee
        // IMPORTANT: All payment related request goes through Payment Service.
        if (savedLateFine.employee == null || savedLateFine.company == null) {
            error("Invalid LateFine")
        }

        val savedPaymentResponse = paymentService.savePayment(SavePaymentRequest(
            employeeId = savedLateFine.employee?.id ?: -1,
            companyId = savedLateFine.company?.id ?: -1,
            forDate = savedLateFine.forDate ?: "",
            paymentType = PaymentType.PAYMENT_ATTENDANCE_LATE_FINE,
            amountInPaisa = savedLateFine.totalLateFineAmountInPaisa,
            description = "Added by system for attendance late fine",
        )) ?: error("Payment was not saved for late fine")

        return lateFineServiceConverter.getSavedLateFineResponse(savedLateFine, savedPaymentResponse)
    }

}
