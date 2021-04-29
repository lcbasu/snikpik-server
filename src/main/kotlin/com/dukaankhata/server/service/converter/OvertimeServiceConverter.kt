package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.entities.Overtime
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OvertimeServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    @Autowired
    private lateinit var paymentServiceConverter: PaymentServiceConverter

    fun getSavedOvertimeResponse(overtime: Overtime?, payment: Payment): SavedOvertimeResponse {
        return SavedOvertimeResponse(
            serverId = overtime?.id?.toString() ?: "-1",
            company = companyServiceConverter.getSavedCompanyResponse(overtime?.company),
            employee = employeeServiceConverter.getSavedEmployeeResponse(overtime?.employee),
            payment = paymentServiceConverter.getSavedPaymentResponse(payment),
            forDate = overtime?.forDate ?: "",
            hourlyOvertimeWageInPaisa = overtime?.hourlyOvertimeWageInPaisa ?: 0,
            totalOvertimeMinutes = overtime?.totalOvertimeMinutes ?: 0,
            totalOvertimeAmountInPaisa = overtime?.totalOvertimeAmountInPaisa ?: 0,
            addedAt = DateUtils.getEpoch(overtime?.addedAt))
    }
}
