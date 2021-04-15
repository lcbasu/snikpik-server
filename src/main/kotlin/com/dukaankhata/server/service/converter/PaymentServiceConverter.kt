package com.dukaankhata.server.service.converter

import MonthPayment
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Company
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

    fun getPaymentSummary(forYear: Int, forMonth: Int, company: Company, monthlyPayments: List<MonthPayment>): PaymentSummaryResponse {
        val companyResponse = companyServiceConverter.getSavedCompanyResponse(company)
//        val monthlyPaymentsResponse = payments.groupBy { DateUtils.parseStandardDate(it.forDate).monthValue }.map {
//            MonthPaymentResponse(
//                monthNumber = it.key,
//                amount = it.value.sumOf { payment ->  payment.amountInPaisa * payment.multiplierUsed }
//            )
//        }
//        val employeePaymentsResponse = payments.groupBy { it.employee }.map { employeePayments ->
//            EmployeePaymentSummaryResponse(
//                employee = employeeServiceConverter.getSavedEmployeeResponse(employeePayments.key),
//                monthlyPayments = employeePayments.value.groupBy { DateUtils.parseStandardDate(it.forDate).monthValue }.map {
//                    MonthPaymentResponse(
//                        monthNumber = it.key,
//                        amount = it.value.sumOf { payment ->  payment.amountInPaisa * payment.multiplierUsed }
//                    )
//                }
//            )
//        }

        val randomDateInMonth = DateUtils.getRandomDateInMonth(forYear = forYear, forMonth = forMonth)
        val prevMonthNumber = randomDateInMonth.minusMonths(1).monthValue

        val employeePaymentsResponse = monthlyPayments.groupBy { it.employee }.map { forEmployee ->
            val employeeMonthlyPayments = getMonthlyPaymentsResponse(forEmployee.value)
            EmployeePaymentSummaryResponse(
                employee = employeeServiceConverter.getSavedEmployeeResponse(forEmployee.key),
                currentMonthNumber = forMonth,
                currentMonthSalary = getTotalAMount(employeeMonthlyPayments, forMonth, MonthlyPaymentType.SALARY),
                currentMonthPayments = getTotalAMount(employeeMonthlyPayments, forMonth, MonthlyPaymentType.PAYMENT),
                prevMonthNumber = prevMonthNumber,
                prevMonthClosing = getTotalAMount(employeeMonthlyPayments, prevMonthNumber, MonthlyPaymentType.CLOSING),
                monthlyPayments = employeeMonthlyPayments,
            )
        }

        val companyMonthlyPayments = getMonthlyPaymentsResponse(monthlyPayments)
        return PaymentSummaryResponse(
            company = companyResponse,
            currentMonthNumber = forMonth,
            currentMonthSalary = getTotalAMount(companyMonthlyPayments, forMonth, MonthlyPaymentType.SALARY),
            currentMonthPayments = getTotalAMount(companyMonthlyPayments, forMonth, MonthlyPaymentType.PAYMENT),
            prevMonthNumber = prevMonthNumber,
            prevMonthClosing = getTotalAMount(companyMonthlyPayments, prevMonthNumber, MonthlyPaymentType.CLOSING),
            monthlyPayments = companyMonthlyPayments,
            employeePayments = employeePaymentsResponse
        )
    }

    fun getTotalAMount(monthlyPayments: List<MonthPaymentResponse>, forMonth: Int, monthlyPaymentType: MonthlyPaymentType): Long {
        var sum = 0L
        monthlyPayments.filter { it.monthNumber == forMonth && it.monthlyPaymentType == monthlyPaymentType }.map {
            sum += it.amount
        }
        return sum
    }

    private fun getMonthlyPaymentsResponse(monthlyPayments: List<MonthPayment>): List<MonthPaymentResponse> {

        val monthPaymentResponse = mutableListOf<MonthPaymentResponse>()

        monthlyPayments.groupBy { it.monthNumber }.map { forMonth ->
            var salary = 0L
            var closing = 0L
            var payments = 0L

            forMonth.value.map {
                salary += it.salaryAmount
                payments += it.paymentsAmount
                closing += it.closingBalance
            }
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = salary,
                monthlyPaymentType = MonthlyPaymentType.SALARY
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = payments,
                monthlyPaymentType = MonthlyPaymentType.PAYMENT
            ))
            monthPaymentResponse.add(MonthPaymentResponse(
                monthNumber = forMonth.key,
                amount = closing,
                monthlyPaymentType = MonthlyPaymentType.CLOSING
            ))
        }

        return monthPaymentResponse
    }
}
