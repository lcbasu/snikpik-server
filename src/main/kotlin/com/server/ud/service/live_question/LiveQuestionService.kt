package com.server.ud.service.live_question

import com.server.ud.dto.SaveLiveQuestionRequest
import com.server.ud.dto.SavedLiveQuestionResponse

abstract class LiveQuestionService {
    abstract fun saveLiveQuestion(request: SaveLiveQuestionRequest): SavedLiveQuestionResponse?
}
