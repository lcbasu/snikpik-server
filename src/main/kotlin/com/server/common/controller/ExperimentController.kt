package com.server.common.controller

import com.server.common.dto.ApplicableExperimentsResponse
import com.server.common.service.ExperimentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("experiment")
class ExperimentController {

    @Autowired
    private lateinit var experimentService: ExperimentService

    @RequestMapping(value = ["/getApplicableExperiments"], method = [RequestMethod.GET])
    fun getApplicableExperiments(): ApplicableExperimentsResponse? {
        return experimentService.getApplicableExperiments()
    }

}
