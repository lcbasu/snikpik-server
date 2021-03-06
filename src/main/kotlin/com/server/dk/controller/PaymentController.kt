package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("payment")
class PaymentController {
    @Autowired
    private lateinit var paymentService: PaymentService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody savePaymentRequest: SavePaymentRequest): SavedPaymentResponse? {
        return paymentService.savePayment(savePaymentRequest)
    }

    @RequestMapping(value = ["/getCompanyPaymentReport"], method = [RequestMethod.POST])
    fun getCompanyPaymentReport(@RequestBody companyPaymentReportRequest: CompanyPaymentReportRequest): CompanyPaymentReportResponse? {
        return paymentService.getCompanyPaymentReport(companyPaymentReportRequest)
    }

    @RequestMapping(value = ["/getEmployeePaymentDetails"], method = [RequestMethod.POST])
    fun getEmployeePaymentDetails(@RequestBody employeePaymentDetailsRequest: EmployeePaymentDetailsRequest): EmployeePaymentDetailsResponse? {
        return paymentService.getEmployeePaymentDetails(employeePaymentDetailsRequest)
    }

    @RequestMapping(value = ["/getEmployeeCompletePaymentDetails"], method = [RequestMethod.POST])
    fun getEmployeeCompletePaymentDetails(@RequestBody employeeCompletePaymentDetailsRequest: EmployeeCompletePaymentDetailsRequest): EmployeeCompletePaymentDetailsResponse? {
        return paymentService.getEmployeeCompletePaymentDetails(employeeCompletePaymentDetailsRequest)
    }
}
