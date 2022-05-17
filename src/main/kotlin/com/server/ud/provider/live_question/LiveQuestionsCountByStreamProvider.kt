package com.server.ud.provider.live_question

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.live_question.LiveQuestionsCountByStreamRepository
import com.server.ud.entities.live_question.LiveQuestionsCountByStream
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LiveQuestionsCountByStreamProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var liveQuestionsCountByStreamRepository: LiveQuestionsCountByStreamRepository

    fun getLiveQuestionsCountByStream(streamId: String): LiveQuestionsCountByStream? =
        try {
            val streamQuestions = liveQuestionsCountByStreamRepository.findAllByStreamId(streamId)
            if (streamQuestions.size > 1) {
                error("More than one questions has same streamId: $streamId")
            }
            streamQuestions.getOrElse(0) {
                val questionsCountByPost = LiveQuestionsCountByStream()
                questionsCountByPost.questionsCount = 0
                questionsCountByPost.streamId = streamId
                questionsCountByPost
            }
        } catch (e: Exception) {
            logger.error("Getting LiveQuestionsCountByStream for $streamId failed.")
            e.printStackTrace()
            null
        }

    fun increaseQuestionCount(streamId: String) {
        liveQuestionsCountByStreamRepository.incrementQuestionCount(streamId)
        logger.warn("Increased question for streamId: $streamId")
        saveLiveQuestionsCountByStreamToFirestore(getLiveQuestionsCountByStream(streamId))
    }

    private fun saveLiveQuestionsCountByStreamToFirestore (questionsCountByPost: LiveQuestionsCountByStream?) {
        GlobalScope.launch {
            if (questionsCountByPost?.streamId == null) {
                logger.error("No stream id found in questionsCountByPost. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("questions_count_by_stream")
                .document(questionsCountByPost.streamId!!)
                .set(questionsCountByPost)
        }
    }

}
