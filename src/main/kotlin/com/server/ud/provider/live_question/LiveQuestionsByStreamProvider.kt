package com.server.ud.provider.live_question

import com.google.firebase.cloud.FirestoreClient
import com.server.common.enums.ReadableIdPrefix
import com.server.ud.dao.live_question.LiveQuestionsByStreamRepository
import com.server.ud.dto.toSavedLiveQuestionResponseForFirebase
import com.server.ud.entities.like.LikesCountByResource
import com.server.ud.entities.live_question.LiveQuestion
import com.server.ud.entities.live_question.LiveQuestionsByStream
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LiveQuestionsByStreamProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var liveQuestionsByStreamRepository: LiveQuestionsByStreamRepository

    @Autowired
    private lateinit var liveQuestionProvider: LiveQuestionProvider

    fun save(liveQuestion: LiveQuestion) : LiveQuestionsByStream? {
        try {
            val liveQuestionsByPost = LiveQuestionsByStream(
                streamId = liveQuestion.streamId,
                questionId = liveQuestion.questionId,
                userId = liveQuestion.userId,
                createdAt = liveQuestion.createdAt,
                text = liveQuestion.text,
                media = liveQuestion.media,
                likesCount = 0,
            )
            val result = liveQuestionsByStreamRepository.save(liveQuestionsByPost)
            saveLiveQuestionsByStreamToFirestore(result)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun delete(liveQuestion: LiveQuestion) {
        try {
            liveQuestionsByStreamRepository.deleteAllByStreamIdAndCreatedAtAndQuestionId(
                liveQuestion.streamId,
                liveQuestion.createdAt,
                liveQuestion.questionId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveLiveQuestionsByStreamToFirestore (liveQuestionsByStream: LiveQuestionsByStream) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("live_streams")
                .document(liveQuestionsByStream.streamId)
                .collection("questions")
                .document(liveQuestionsByStream.questionId)
                .set(liveQuestionsByStream.toSavedLiveQuestionResponseForFirebase())
        }
    }

    fun updateLikeCountForQuestionByStreamToFirestore (likesCountByResource: LikesCountByResource) {
        GlobalScope.launch {
            if (likesCountByResource.resourceId?.startsWith(ReadableIdPrefix.QLI.name) == true) {
                val question = liveQuestionProvider.getLiveQuestion(likesCountByResource.resourceId!!) ?: error("Question not found for id: ${likesCountByResource.resourceId}")
                FirestoreClient.getFirestore()
                    .collection("live_streams")
                    .document(question.streamId)
                    .collection("questions")
                    .document(question.questionId)
                    .update("likesCount", likesCountByResource.likesCount)
            }
        }
    }
}
