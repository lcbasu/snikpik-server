package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.EmployeePaymentSummaryResponse
import com.dukaankhata.server.dto.MonthPayment
import com.dukaankhata.server.dto.PaymentSummaryResponse
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Payment
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
            serverId = payment?.id ?: -1L,
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

    fun getPaymentSummary(company: Company, payments: List<Payment>): PaymentSummaryResponse {
        val companyResponse = companyServiceConverter.getSavedCompanyResponse(company)
        val monthlyPaymentsResponse = payments.groupBy { DateUtils.parseStandardDate(it.forDate).monthValue }.map {
            MonthPayment(
                monthNumber = it.key,
                amount = it.value.sumOf { payment ->  payment.amountInPaisa * payment.multiplierUsed }
            )
        }
        val employeePaymentsResponse = payments.groupBy { it.employee }.map { employeePayments ->
            EmployeePaymentSummaryResponse(
                employee = employeeServiceConverter.getSavedEmployeeResponse(employeePayments.key),
                monthlyPayments = employeePayments.value.groupBy { DateUtils.parseStandardDate(it.forDate).monthValue }.map {
                    MonthPayment(
                        monthNumber = it.key,
                        amount = it.value.sumOf { payment ->  payment.amountInPaisa * payment.multiplierUsed }
                    )
                }
            )
        }
        return PaymentSummaryResponse(
            company = companyResponse,
            monthlyPayments = monthlyPaymentsResponse,
            employeePayments = employeePaymentsResponse
        )
    }
}
