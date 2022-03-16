package com.server.common.config

import com.server.common.properties.PaymentProperties
import com.razorpay.RazorpayClient
import com.razorpay.RazorpayException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaymentConfig {

    @Autowired
    private lateinit var paymentProperties: PaymentProperties

    @Bean
    fun getRazorpayClient(): RazorpayClient? {
        return try {
            RazorpayClient(paymentProperties.razorpay.key, paymentProperties.razorpay.secret)
        } catch (e: RazorpayException) {
            e.printStackTrace()
            null
        }
    }
}
