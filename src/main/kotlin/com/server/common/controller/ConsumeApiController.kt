package com.server.common.controller

import com.amazonaws.auth.PropertiesCredentials
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
            logger.info("messageType: ${message["Type"]}")

            val bodyObject = try {
                jacksonObjectMapper().readValue(stream, ProcessedVideoSNSRequestBody::class.java)
            } catch (e: Exception) {
                null
            }

            logger.info("bodyObject: ${bodyObject.toString()}")
            logger.info("MessageId: ${bodyObject?.MessageId}")

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }




//        val body = getBody(request)
//        logger.info("body: $body")
//        logger.info("Call mediaHandlerService for InputFile: ${bodyObject?.Message?.InputFile}")
//        mediaHandlerService.startProcessingAfterVideoProcessing(bodyObject?.Message)
    }

    @Throws(IOException::class)
    fun getBody(request: HttpServletRequest): String? {
        try {
            val inputStream: InputStream = request.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bufferedReader.readLine()
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            logger.error("Error while reading request body", e)
            return null
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProcessedVideoSNSRequestBody (
    val Type : String?,
    val MessageId : String?,
    val TopicArn : String?,
    val Subject : String?,
    val Message : ProcessedVideoMessage?,
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
    val InputDetails : InputDetails?,
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
