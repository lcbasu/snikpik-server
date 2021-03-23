package com.dukaankhata.server.service

import com.dukaankhata.server.dto.PaymentSummaryRequest
import com.dukaankhata.server.dto.PaymentSummaryResponse
import com.dukaankhata.server.dto.SavePaymentRequest
import com.dukaankhata.server.dto.SavedPaymentResponse

abstract class PaymentService {
    abstract fun savePayment(savePaymentRequest: SavePaymentRequest): SavedPaymentResponse?
    abstract fun getPaymentSummary(paymentSummaryRequest: PaymentSummaryRequest): PaymentSummaryResponse?
}
