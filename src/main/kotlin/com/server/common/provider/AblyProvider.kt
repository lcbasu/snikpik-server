package com.server.common.provider

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ably.lib.rest.AblyRest
import io.ably.lib.types.AblyException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AblyProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var ablyRest: AblyRest

    fun publishToChannel(channelName: String, name: String, data: Any): Boolean {
        try {
            ablyRest.channels.get(channelName).publish(name, jacksonObjectMapper().writeValueAsString(data))
        } catch (error: AblyException) {
            logger.error("Error while publishing message to able")
            logger.error(error.message)
            error.printStackTrace()
            return false
        }
        return true
    }

}
