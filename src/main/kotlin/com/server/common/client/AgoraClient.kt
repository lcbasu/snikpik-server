package com.server.common.client

import com.server.common.properties.AgoraProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class AgoraClient {

    @Autowired
    private lateinit var agoraProperties: AgoraProperties

    // https://console.agora.io/restfulApi
    fun getAuthorizationHeader(): String {
        val customerKey = agoraProperties.customerKey
        val customerSecret = agoraProperties.customerSecret
        val plainCredentials = "$customerKey:$customerSecret"
        val base64Credentials = String(Base64.getEncoder().encode(plainCredentials.toByteArray()))
        return "Basic $base64Credentials"
    }

}
