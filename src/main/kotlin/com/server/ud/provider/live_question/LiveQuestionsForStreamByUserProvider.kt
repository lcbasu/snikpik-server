package com.server.ud.provider.live_question

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.live_question.LiveQuestionForStreamByUserRepository
import com.server.ud.entities.live_question.LiveQuestionForStreamByUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LiveQuestionsForStreamByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var liveQuestionForStreamByUserRepository: LiveQuestionForStreamByUserRepository

    fun getLiveQuestionForStreamByUser(streamId: String, userId: String): LiveQuestionForStreamByUser? =
        try {
            val questionAsked = liveQuestionForStreamByUserRepository.findAllByStreamIdAndUserId(streamId, userId)
            if (questionAsked.size > 1) {
                error("More than one liveQuestions has same streamId: $streamId by the userId: $userId")
            }
            questionAsked.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting LiveQuestionForStreamByUser for $streamId & userId: $userId failed.")
            e.printStackTrace()
            null
        }

    fun save(streamId: String, userId: String, asked: Boolean) : LiveQuestionForStreamByUser? {
        return try {
            val liveQuestion = LiveQuestionForStreamByUser(
                streamId = streamId,
                userId = userId,
                asked = asked,
            )
            val result = liveQuestionForStreamByUserRepository.save(liveQuestion)
            saveLiveQuestionForStreamByUserToFirestore(result)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setLiveQuestioned(streamId: String, userId: String) {
        save(streamId, userId, true)
    }

    private fun saveLiveQuestionForStreamByUserToFirestore (liveQuestionForStreamByUser: LiveQuestionForStreamByUser) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("users")
                .document(liveQuestionForStreamByUser.userId)
                .collection("live_question_for_stream_by_user")
                .document(liveQuestionForStreamByUser.streamId)
                .set(liveQuestionForStreamByUser)
        }
    }

    fun saveAllToFirestore() {
        liveQuestionForStreamByUserRepository.findAll().forEach {
            saveLiveQuestionForStreamByUserToFirestore(it!!)
        }
    }

}
