package com.dukaankhata.server.service.converter

import MonthPayment
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.enums.MonthlyPaymentType
import com.dukaankhata.server.enums.PaymentType
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PaymentServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    fun getSavedPaymentResponse(payment: Payment?): SavedPaymentResponse {
        return SavedPaymentResponse(
            serverId = payment?.id ?: "",
            employee = employeeServiceConverter.getSavedEmployeeResponse(payment?.employee),
            company = companyServiceConverter.getSavedCompanyResponse(payment?.company),
            forDate = payment?.forDate ?: "",
            paymentType = payment?.paymentType ?: PaymentType.NONE,
            description = payment?.description,
            amountInPaisa = payment?.amountInPaisa ?: 0,
            multiplierUsed = payment?.multiplierUsed ?: 0,
            addedAt = DateUtils.getEpoch(payment?.addedAt),
        )
    }
}
