package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.OvertimeRepository
import com.server.dk.dto.SaveOvertimeRequest
import com.server.dk.dto.SavedOvertimeResponse
import com.server.dk.dto.toSavedOvertimeResponse
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.Overtime
import com.server.common.entities.User
import com.server.dk.enums.PaymentType
import com.server.common.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OvertimeProvider {

    @Autowired
    private lateinit var overtimeRepository: OvertimeRepository

    @Autowired
    private lateinit var paymentProvider: PaymentProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getOvertime(overtimeId: String): Overtime? =
        try {
            overtimeRepository.findById(overtimeId).get()
        } catch (e: Exception) {
            null
        }

    fun saveOvertime(addedBy: User, company: Company, employee: Employee, forDate: String, saveOvertimeRequest: SaveOvertimeRequest) : SavedOvertimeResponse {
        val overtime = Overtime()
        overtime.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.OVT.name)
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

        val savedPayment = paymentProvider.savePaymentAndDependentData(
            addedBy = addedBy,
            company = company,
            employee = employee,
            forDate = savedOvertime.forDate,
            paymentType = PaymentType.PAYMENT_ATTENDANCE_OVERTIME,
            amountInPaisa = savedOvertime.totalOvertimeAmountInPaisa,
            description = "Added by system for attendance overtime",
        )

        return savedOvertime.toSavedOvertimeResponse(savedPayment)
    }

    suspend fun getAllOvertimesForDate(company: Company, forDate: String): List<Overtime> {
        return try {
            overtimeRepository.getAllOvertimesForDate(company.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }
    }

//    fun getOvertimesForEmployee(employee: Employee, forDate: String): List<Overtime> {
//        return try {
//            overtimeRepository.getOvertimesForEmployee(employee.id, forDate)
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

//    fun getOvertimesForEmployee(employee: Employee, startTime: LocalDateTime, endTime: LocalDateTime): List<Overtime> {
//        return try {
//            overtimeRepository.getOvertimesForEmployee(employee.id, startTime, endTime)
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

    fun getOvertimesForEmployee(employee: Employee, datesList: List<String>): List<Overtime> {
        return try {
            overtimeRepository.getOvertimesForEmployee(employee.id, datesList)
        } catch (e: Exception) {
            emptyList()
        }
    }

}
