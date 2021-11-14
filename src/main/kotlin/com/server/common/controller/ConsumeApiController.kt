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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
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
        val body = getBody(request)
        logger.info("body: $body")
        val bodyObject = try {
            jacksonObjectMapper().readValue(body, ProcessedVideoSNSRequestBody::class.java)
        } catch (e: Exception) {
            null
        }
        logger.info("Call mediaHandlerService for InputFile: ${bodyObject?.Message?.InputFile}")
        mediaHandlerService.startProcessingAfterVideoProcessing(bodyObject?.Message)
    }

    @Throws(IOException::class)
    fun getBody(request: HttpServletRequest): String? {
        var body: String? = null
        val stringBuilder = StringBuilder()
        var bufferedReader: BufferedReader? = null
        try {
            val inputStream: InputStream? = request.inputStream
            if (inputStream != null) {
                bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val charBuffer = CharArray(128)
                var bytesRead = -1
                while (bufferedReader.read(charBuffer).also { bytesRead = it } > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead)
                }
            } else {
                stringBuilder.append("")
            }
        } catch (ex: IOException) {
            throw ex
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (ex: IOException) {
                    throw ex
                }
            }
        }
        body = stringBuilder.toString()
        return body
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
