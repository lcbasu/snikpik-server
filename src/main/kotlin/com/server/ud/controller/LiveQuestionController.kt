package com.server.ud.controller

import com.server.ud.dto.SaveLiveQuestionRequest
import com.server.ud.dto.SavedLiveQuestionResponse
import com.server.ud.service.live_question.LiveQuestionService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/liveQuestion")
class LiveQuestionController {

    @Autowired
    private lateinit var liveQuestionService: LiveQuestionService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLiveQuestion(@RequestBody request: SaveLiveQuestionRequest): SavedLiveQuestionResponse? {
        return liveQuestionService.saveLiveQuestion(request)
    }
}
