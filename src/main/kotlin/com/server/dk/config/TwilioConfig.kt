package com.server.dk.config

import com.server.dk.properties.TwilioProperties
import com.twilio.Twilio
import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TwilioConfig {

    @Autowired
    private lateinit var twilioProperties: TwilioProperties

    @Primary
    @Bean
    fun twilioInit() {
        try {
            Twilio.init(twilioProperties.accountSid, twilioProperties.authToken)
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
        }
    }

}
