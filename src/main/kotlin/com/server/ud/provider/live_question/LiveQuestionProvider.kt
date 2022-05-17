package com.server.ud.provider.live_question

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.live_question.LiveQuestionRepository
import com.server.ud.dto.SaveLiveQuestionRequest
import com.server.ud.entities.live_question.LiveQuestion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LiveQuestionProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var liveQuestionRepository: LiveQuestionRepository

    @Autowired
    private lateinit var liveQuestionsByStreamProvider: LiveQuestionsByStreamProvider

    @Autowired
    private lateinit var liveQuestionsByUserProvider: LiveQuestionsByUserProvider

    @Autowired
    private lateinit var liveQuestionsCountByStreamProvider: LiveQuestionsCountByStreamProvider

    @Autowired
    private lateinit var liveQuestionsForStreamByUserProvider: LiveQuestionsForStreamByUserProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun getLiveQuestion(questionId: String): LiveQuestion? =
        try {
            val questions = liveQuestionRepository.findAllByQuestionId(questionId)
            if (questions.size > 1) {
                error("More than one question has same questionId: $questionId")
            }
            questions.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting StreamLiveQuestion for $questionId failed.")
            e.printStackTrace()
            null
        }


    fun saveLiveQuestion(request: SaveLiveQuestionRequest): LiveQuestion? {
        val userDetailsFromToken = securityProvider.validateRequest()
        return save(userDetailsFromToken.getUserIdToUse(), request)
    }

    private fun save(userId: String, request: SaveLiveQuestionRequest) : LiveQuestion? {
        try {
            val question = LiveQuestion(
                questionId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.QLI.name),
                userId = userId,
                createdAt = DateUtils.getInstantNow(),
                streamId = request.streamId,
                text = request.text,
                media = request.mediaDetails?.convertToString(),
            )
            val savedLiveQuestion = liveQuestionRepository.save(question)
            processLiveQuestion(savedLiveQuestion)
            return savedLiveQuestion
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun delete(question: LiveQuestion) {
        try {
            delete(question.questionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete(questionId: String) {
        try {
            liveQuestionRepository.deleteByQuestionId(questionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun processLiveQuestion(liveQuestion: LiveQuestion) {
        GlobalScope.launch {
            logger.info("Start: question processing for questionId: ${liveQuestion.questionId}")
            val questionsByStreamFuture = async { liveQuestionsByStreamProvider.save(liveQuestion) }
            val questionsCountByStreamFuture = async { liveQuestionsCountByStreamProvider.increaseQuestionCount(liveQuestion.streamId) }
            val questionsByUserFuture = async { liveQuestionsByUserProvider.save(liveQuestion) }
            val questionForStreamByUserFuture = async { liveQuestionsForStreamByUserProvider.setLiveQuestioned(liveQuestion.streamId, liveQuestion.userId) }
            questionsByStreamFuture.await()
            questionsByUserFuture.await()
            questionForStreamByUserFuture.await()
            questionsCountByStreamFuture.await()
            logger.info("Done: question processing for questionId: ${liveQuestion.questionId}")
        }
    }

//    fun processLiveQuestionNowAfterUpdating(question: LiveQuestion) {
//        runBlocking {
//            logger.info("StartNow: question processing for questionId: ${question.questionId}")
//            udJobProvider.scheduleProcessingForLiveQuestion(question.questionId)
//            liveQuestionsByStreamProvider.save(question)
//            logger.info("DoneNow: question processing for questionId: ${question.questionId}")
//        }
//    }

//    fun deleteAllLiveQuestionsOfStream(streamId: String) {
//        GlobalScope.launch {
//            val questionsByStream = liveQuestionsByStreamProvider.getAllLiveQuestionsByStream(streamId)
//            questionsByStream.map {
//                deleteLiveQuestionAndAllExpandedData(it.questionId)
//            }
//        }
//    }

//    fun deleteLiveQuestionAndAllExpandedData(questionId: String) {
//        GlobalScope.launch {
//            deleteLiveQuestionAndAllExpandedData(getLiveQuestion(questionId) ?: error("Failed to get question for questionId: $questionId"))
//        }
//    }

//    private fun deleteLiveQuestionAndAllExpandedData(question: LiveQuestion) {
//        GlobalScope.launch {
//            val userId = question.userId
//
//            bookmarkProvider.deleteResourceExpandedData(question.questionId)
//            likeProvider.deleteResourceExpandedData(question.questionId)
//            userActivitiesProvider.deleteLiveQuestionExpandedData(question.questionId)
//
//            val allLiveQuestionsByUser =  liveQuestionsByUserProvider.deleteAllLiveQuestionsByUser(question)
//            if (allLiveQuestionsByUser.isEmpty() || allLiveQuestionsByUser.size == 1 && allLiveQuestionsByUser.first().questionId == question.questionId) {
//                liveQuestionsForStreamByUserProvider.resetLiveQuestioned(question.streamId, userId)
//            } else {
//                liveQuestionsForStreamByUserProvider.setLiveQuestioned(question.streamId, userId)
//            }
//
//            liveQuestionsCountByStreamProvider.decreaseLiveQuestionCount(question.streamId)
//
//            liveQuestionsByStreamProvider.delete(question)
//
//            replyProvider.deleteAllRepliesOfLiveQuestion(question.questionId)
//
//            delete(question)
//        }
//    }

//    fun deleteLiveQuestion(request: DeleteLiveQuestionRequest): DeleteLiveQuestionResponse {
//        val userDetailsFromToken = securityProvider.validateRequest()
//        val question = getLiveQuestion(request.questionId) ?: error("Failed to get question for questionId: ${request.questionId}")
//        if (question.userId != userDetailsFromToken.getUserIdToUse()) {
//            error("You are not allowed to delete this question. Only the owner can delete the question. questionId: ${request.questionId}, ownerId: ${question.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
//        }
//        deleteLiveQuestionAndAllExpandedData(request.questionId)
//        return DeleteLiveQuestionResponse(request.questionId, true)
//    }


//    fun updateLiveQuestion(request: UpdateLiveQuestionRequest): LiveQuestion? {
//        return try {
//            val question = getLiveQuestion(request.questionId) ?: error("Failed to get questions data for questionId: ${request.questionId}")
//            val userDetailsFromToken = securityProvider.validateRequest()
//            if (question.userId != userDetailsFromToken.getUserIdToUse()) {
//                error("You are not allowed to update this question. Only the owner can update the question. questionId: ${request.questionId}, ownerId: ${question.userId}, userId: ${userDetailsFromToken.getUserIdToUse()}")
//            }
//            val savedLiveQuestion = questionRepository.save(question.copy(
//                text = request.text,
//                media = request.mediaDetails?.convertToString(),
//            ))
//            processLiveQuestionNowAfterUpdating(savedLiveQuestion)
//            savedLiveQuestion
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

}
