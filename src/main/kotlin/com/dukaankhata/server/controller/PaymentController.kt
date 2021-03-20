package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse
import com.dukaankhata.server.service.PaymentService
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
}
