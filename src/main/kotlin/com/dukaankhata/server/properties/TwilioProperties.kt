package com.dukaankhata.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("twilio")
data class TwilioProperties(var accountSid: String? = null, var authToken: String? = null)
