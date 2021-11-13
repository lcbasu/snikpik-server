package com.server.common.controller

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.server.common.service.MediaHandlerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("consumeAPI")
class ConsumeApiController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var mediaHandlerService: MediaHandlerService

    @RequestMapping(value = ["/awsVideoProcessingCompleted"], method = [RequestMethod.POST])
    @ResponseBody
    fun awsVideoProcessingCompleted(@RequestBody request: ProcessedVideoSNSRequest, response: HttpServletResponse){
        logger.info("Call mediaHandlerService for InputFile: ${request.Message?.InputFile}")
        mediaHandlerService.startProcessingAfterVideoProcessing(request.Message)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProcessedVideoSNSRequest (
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
