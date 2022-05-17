package com.server.ud.service.live_question

import com.server.ud.dto.SaveLiveQuestionRequest
import com.server.ud.dto.SavedLiveQuestionResponse
import com.server.ud.dto.toSavedLiveQuestionResponse
import com.server.ud.provider.live_question.LiveQuestionProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LiveQuestionServiceImpl : LiveQuestionService() {

    @Autowired
    private lateinit var liveQuestionProvider: LiveQuestionProvider

    override fun saveLiveQuestion(request: SaveLiveQuestionRequest): SavedLiveQuestionResponse? {
        return liveQuestionProvider.saveLiveQuestion(request)?.toSavedLiveQuestionResponse()
    }

}
