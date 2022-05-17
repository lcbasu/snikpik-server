package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.live_question.LiveQuestion
import com.server.ud.entities.live_question.LiveQuestionsByStream
import com.server.ud.entities.live_question.getMediaDetails

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveLiveQuestionRequest (
    val streamId: String,
    var text: String?,
    var mediaDetails: MediaDetailsV2? = null,
)

data class SavedLiveQuestionResponse (
    val questionId: String,
    val streamId: String,
    val createdAt: Long,
    var userId: String,
    var text: String?,
    var mediaDetails: MediaDetailsV2? = null,
)

data class SavedLiveQuestionResponseForFirebase (
    val questionId: String,
    val streamId: String,
    val createdAt: Long,
    var userId: String,
    var likesCount: Long,
    var text: String?,
    var mediaDetails: MediaDetailsV2? = null,
)

fun LiveQuestion.toSavedLiveQuestionResponse(): SavedLiveQuestionResponse {
    this.apply {
        return SavedLiveQuestionResponse(
            questionId = questionId,
            createdAt = DateUtils.getEpoch(createdAt),
            streamId = streamId,
            userId = userId,
            text = text,
            mediaDetails = getMediaDetails(),
        )
    }
}


fun LiveQuestionsByStream.toSavedLiveQuestionResponseForFirebase(): SavedLiveQuestionResponseForFirebase {
    this.apply {
        return SavedLiveQuestionResponseForFirebase(
            questionId = questionId,
            createdAt = DateUtils.getEpoch(createdAt),
            streamId = streamId,
            userId = userId,
            text = text,
            // Doing this so that value of zero can be saved in Firebase which helps in ordering in UI
            likesCount = likesCount,
            mediaDetails = getMediaDetails(),
        )
    }
}

