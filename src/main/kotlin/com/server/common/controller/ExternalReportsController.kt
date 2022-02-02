package com.server.common.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("externalReposts")
class ExternalReportsController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping(value = ["/otpDelivery"], method = [RequestMethod.POST])
    @ResponseBody
    fun otpDelivery(request: HttpServletRequest, response: HttpServletResponse){
        try {
            logger.info("otpDelivery: request")
            logger.info(request.toString())
            // Scan request into a string
            val scanner = Scanner(request.inputStream)
            val builder = StringBuilder()
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine())
            }
            logger.info("builder: ${builder.toString()}")

            val msgStr = builder.substring(6, builder.length-1)

            // Parse the JSON message
            val stream: InputStream = ByteArrayInputStream(msgStr.toString().toByteArray())
            val message = jacksonObjectMapper().readValue(stream, Msg91SMSDeliveryObject::class.java)

            logger.info("message: ${message.toString()}")
        } catch (e: java.lang.Exception) {
            logger.error("Error in otpDelivery")
            e.printStackTrace()
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Msg91SMSDeliveryObject (
    val senderId : String?,
    val requestId : String?,
    val report : List<Msg91SMSDeliveryReport>? = emptyList(),
    val userId : String?,
    val campaignName : String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Msg91SMSDeliveryReport (
    val date : String?,
    val number : String?,
    val status : String?,
    val desc : String?,
)

