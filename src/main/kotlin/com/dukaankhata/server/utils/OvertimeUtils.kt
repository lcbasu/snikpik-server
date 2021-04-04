package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.OvertimeRepository
import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Overtime
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.service.PaymentService
import com.dukaankhata.server.service.converter.OvertimeServiceConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OvertimeUtils {

    @Autowired
    private lateinit var overtimeRepository: OvertimeRepository

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var overtimeServiceConverter: OvertimeServiceConverter

    fun getOvertime(overtimeId: Long): Overtime? =
        try {
            overtimeRepository.findById(overtimeId).get()
        } catch (e: Exception) {
            null
        }

    fun saveOvertime(addedBy: User, company: Company, employee: Employee, forDate: String, saveOvertimeRequest: SaveOvertimeRequest) : SavedOvertimeResponse {
        val overtime = Overtime()
        overtime.company = company
        overtime.employee = employee
        overtime.addedBy = addedBy
        overtime.forDate = forDate
        overtime.hourlyOvertimeWageInPaisa = saveOvertimeRequest.hourlyOvertimeWageInPaisa
        overtime.totalOvertimeMinutes = saveOvertimeRequest.totalOvertimeMinutes
        overtime.totalOvertimeAmountInPaisa = saveOvertimeRequest.hourlyOvertimeWageInPaisa * (saveOvertimeRequest.totalOvertimeMinutes / 60)
        val savedOvertime = overtimeRepository.save(overtime)


        // Save Payment for the employee
        // IMPORTANT: All payment related request goes through Payment Service.
        if (savedOvertime.employee == null || savedOvertime.company == null) {
            error("Invalid Overtime")
        }

        val savedPaymentResponse = paymentService.savePayment(SavePaymentRequest(
            employeeId = savedOvertime.employee?.id ?: -1,
            companyId = savedOvertime.company?.id ?: -1,
            forDate = savedOvertime.forDate ?: "",
            paymentType = PaymentType.PAYMENT_ATTENDANCE_OVERTIME,
            amountInPaisa = savedOvertime.totalOvertimeAmountInPaisa,
            description = "Added by system for attendance overtime",
        )) ?: error("Payment was not saved for overtime")

        return overtimeServiceConverter.getSavedOvertimeResponse(savedOvertime, savedPaymentResponse)
    }

    fun getAllOvertimesForDate(company: Company, forDate: String): List<Overtime> {
        return try {
            overtimeRepository.getAllOvertimesForDate(company.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }
    }

}
