package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.entities.Payment
import com.dukaankhata.server.enums.PaymentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
class PaymentServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    fun getSavedPaymentResponse(payment: Payment?): SavedPaymentResponse {
        return SavedPaymentResponse(
            serverId = payment?.id ?: -1L,
            employee = employeeServiceConverter.getSavedEmployeeResponse(payment?.employee),
            company = companyServiceConverter.getSavedCompanyResponse(payment?.company),
            forDate = payment?.forDate ?: "",
            paymentType = payment?.paymentType ?: PaymentType.NONE,
            description = payment?.description,
            amountInPaisa = payment?.amountInPaisa ?: 0,
            multiplierUsed = payment?.multiplierUsed ?: 0,
            addedAt = payment?.addedAt?.toEpochSecond(ZoneOffset.UTC) ?: 0,
        )
    }
}
