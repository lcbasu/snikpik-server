package com.server.common.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("consumeAPI")
class ConsumeApiController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping(value = ["/awsVideoProcessingCompleted"], method = [RequestMethod.POST])
    @ResponseBody
    fun awsVideoProcessingCompleted(request: HttpServletRequest, response: HttpServletResponse){
        val body = request.getParameter("body")
        val lang = request.getParameter("lang")
        logger.info("$request")
        logger.info(body)
        logger.info("${request.headerNames}")
    }
}
