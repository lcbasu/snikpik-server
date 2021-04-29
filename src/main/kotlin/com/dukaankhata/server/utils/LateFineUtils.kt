package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.LateFineRepository
import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.LateFine
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.service.converter.LateFineServiceConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LateFineUtils {

    @Autowired
    private lateinit var lateFineRepository: LateFineRepository

    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    @Autowired
    private lateinit var lateFineServiceConverter: LateFineServiceConverter

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getLateFine(lateFineId: String): LateFine? =
        try {
            lateFineRepository.findById(lateFineId).get()
        } catch (e: Exception) {
            null
        }

    fun saveLateFine(addedBy: User, company: Company, employee: Employee, forDate: String, saveLateFineRequest: SaveLateFineRequest) : SavedLateFineResponse {

        val hourlyLateFineWageInPaisa = 5L // TODO: Update based on salary

        val lateFine = LateFine()
        lateFine.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.LFN.name)
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

        val savedPaymentResponse = paymentUtils.savePaymentAndDependentData(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = savedLateFine.forDate,
            paymentType = PaymentType.PAYMENT_ATTENDANCE_LATE_FINE,
            amountInPaisa = savedLateFine.totalLateFineAmountInPaisa,
            description = "Added by system for attendance late fine",
        )

        return lateFineServiceConverter.getSavedLateFineResponse(savedLateFine, savedPaymentResponse)
    }

    fun getAllLateFineForDate(company: Company, forDate: String): List<LateFine> {
        return try {
            lateFineRepository.getAllLateFineForDate(company.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLateFinesForEmployee(employee: Employee, forDate: String): List<LateFine> {
        return try {
            lateFineRepository.getLateFinesForEmployee(employee.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLateFinesForEmployee(employee: Employee, startTime: LocalDateTime, endTime: LocalDateTime): List<LateFine> {
        return try {
            lateFineRepository.getLateFinesForEmployee(employee.id, startTime, endTime)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLateFinesForEmployee(employee: Employee, datesList: List<String>): List<LateFine> {
        return try {
            lateFineRepository.getLateFinesForEmployee(employee.id, datesList)
        } catch (e: Exception) {
            emptyList()
        }
    }

}
