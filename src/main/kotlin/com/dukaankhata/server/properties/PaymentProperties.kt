package com.dukaankhata.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("payments")
data class PaymentProperties(val razorpay: RazorpayProperties) {
    data class RazorpayProperties(var key: String, var secret: String? = null)
}
