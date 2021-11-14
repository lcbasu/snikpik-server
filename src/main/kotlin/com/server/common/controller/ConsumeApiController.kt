package com.server.common.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.service.MediaHandlerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("consumeAPI")
class ConsumeApiController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var mediaHandlerService: MediaHandlerService

    @RequestMapping(value = ["/awsVideoProcessingCompleted"], method = [RequestMethod.POST])
    @ResponseBody
    fun awsVideoProcessingCompleted(request: HttpServletRequest, response: HttpServletResponse){
        try {
            // Scan request into a string
            val scanner = Scanner(request.inputStream)
            val builder = StringBuilder()
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine())
            }

            logger.info("builder: ${builder.toString()}")

            // Parse the JSON message
            val stream: InputStream = ByteArrayInputStream(builder.toString().toByteArray())
            val message = jacksonObjectMapper().readValue(stream, MutableMap::class.java)

            logger.info("message: ${message.toString()}")
            logger.info("messageType: ${message.getOrDefault("Type", "NOT_FOUND")}")
            val messageMessage = message.getOrDefault("Message", "NOT_FOUND")
            logger.info("messageMessage: $messageMessage")
            val processedVideoMessage = getProcessedVideoMessage(messageMessage.toString())
            logger.info("processedVideoMessage: ${processedVideoMessage}")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
//        val body = getBody(request)
//        logger.info("body: $body")
//        logger.info("Call mediaHandlerService for InputFile: ${bodyObject?.Message?.InputFile}")
//        mediaHandlerService.startProcessingAfterVideoProcessing(bodyObject?.Message)
    }

    fun getProcessedVideoMessage(message: String): ProcessedVideoMessage? {
        logger.info("message: $message")
        return try {
            val messageObject = jacksonObjectMapper().readValue(message, MutableMap::class.java)
            return messageObject?.let {
                logger.info("messageObject: $messageObject")
                val Outputs = messageObject["Outputs"] as Map<*, *>
                logger.info("Outputs: $Outputs")
                val HLS_GROUP = Outputs["HLS_GROUP"] as List<*>
                logger.info("HLS_GROUP: $HLS_GROUP")
                ProcessedVideoMessage(
                    Id = messageObject.getOrDefault("Id", "NOT_FOUND").toString(),
                    InputFile = messageObject.getOrDefault("InputFile", "NOT_FOUND").toString(),
                    Outputs = Outputs(
                        HLS_GROUP.map { it.toString() }
                    ),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProcessedVideoSNSRequestBody (
    val Type : String?,
    val MessageId : String?,
    val TopicArn : String?,
    val Subject : String?,
//    val Message : ProcessedVideoMessage?,
    val Timestamp : String?,
    val SignatureVersion : Int?,
    val Signature : String?,
    val SigningCertURL : String?,
    val UnsubscribeURL : String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProcessedVideoMessage (
    val Id : String?,
    val InputFile : String?,
    val InputDetails : InputDetails? = null,
    val Outputs : Outputs?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InputDetails (
    val id : Int?,
    val video : List<InputVideoDetail?>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Outputs (
    @JsonProperty("HLS_GROUP")
    val processedVideoUrls : List<String?>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InputVideoDetail (
    val bitDepth : Int?,
    val codec : String?,
    val colorFormat : String?,
    val fourCC : String?,
    val frameRate : Double?,
    val height : Int?,
    val interlaceMode : String?,
    val sar : String?,
    val standard : String?,
    val streamId : Int?,
    val width : Int?
)
