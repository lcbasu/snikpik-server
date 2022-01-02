package com.server.common.config

import com.server.common.properties.AblyProperties
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.rest.AblyRest
import io.ably.lib.types.AblyException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AblyConfig {

    @Autowired
    private lateinit var ablyProperties: AblyProperties

    @Bean
    protected fun ablyRealtime(): AblyRealtime? {
        return try {
            AblyRealtime(ablyProperties.apiKey)
        } catch (exception: AblyException) {
            null
        }
    }

    @Bean
    fun ablyRest(): AblyRest? {
        return try {
            AblyRest(ablyProperties.apiKey)
        } catch (exception: AblyException) {
            null
        }
    }
}
