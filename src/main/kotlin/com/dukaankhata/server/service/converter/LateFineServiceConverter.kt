package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedLateFineResponse
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.entities.LateFine
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LateFineServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    @Autowired
    private lateinit var paymentServiceConverter: PaymentServiceConverter

    fun getSavedLateFineResponse(lateFine: LateFine?, payment: Payment): SavedLateFineResponse {
        return SavedLateFineResponse(
            serverId = lateFine?.id?.toString() ?: "-1",
            company = companyServiceConverter.getSavedCompanyResponse(lateFine?.company),
            employee = employeeServiceConverter.getSavedEmployeeResponse(lateFine?.employee),
            payment = paymentServiceConverter.getSavedPaymentResponse(payment),
            forDate = lateFine?.forDate ?: "",
            hourlyLateFineWageInPaisa = lateFine?.hourlyLateFineWageInPaisa ?: 0,
            totalLateFineMinutes = lateFine?.totalLateFineMinutes ?: 0,
            totalLateFineAmountInPaisa = lateFine?.totalLateFineAmountInPaisa ?: 0,
            addedAt = DateUtils.getEpoch(lateFine?.addedAt))
    }
}
