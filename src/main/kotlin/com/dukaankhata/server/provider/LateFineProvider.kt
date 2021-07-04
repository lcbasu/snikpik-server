package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.LateFineRepository
import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.dto.toSavedLateFineResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.LateFine
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LateFineProvider {

    @Autowired
    private lateinit var lateFineRepository: LateFineRepository

    @Autowired
    private lateinit var paymentProvider: PaymentProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getLateFine(lateFineId: String): LateFine? =
        try {
            lateFineRepository.findById(lateFineId).get()
        } catch (e: Exception) {
            null
        }

    fun saveLateFine(addedBy: User, company: Company, employee: Employee, forDate: String, saveLateFineRequest: SaveLateFineRequest) : SavedLateFineResponse {

        val hourlyLateFineWageInPaisa = 5L // TODO: Update based on salary

        val lateFine = LateFine()
        lateFine.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.LFN.name)
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

        val savedPaymentResponse = paymentProvider.savePaymentAndDependentData(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = savedLateFine.forDate,
            paymentType = PaymentType.PAYMENT_ATTENDANCE_LATE_FINE,
            amountInPaisa = savedLateFine.totalLateFineAmountInPaisa,
            description = "Added by system for attendance late fine",
        )

        return savedLateFine.toSavedLateFineResponse(savedPaymentResponse)
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
