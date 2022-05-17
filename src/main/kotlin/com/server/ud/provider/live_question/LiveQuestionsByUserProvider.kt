package com.server.ud.provider.live_question

import com.server.ud.dao.live_question.LiveQuestionsByUserRepository
import com.server.ud.entities.live_question.LiveQuestion
import com.server.ud.entities.live_question.LiveQuestionsByUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LiveQuestionsByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var liveQuestionsByUserRepository: LiveQuestionsByUserRepository

    fun save(question: LiveQuestion) : LiveQuestionsByUser? {
        try {
            val questionsByUser = LiveQuestionsByUser(
                userId = question.userId,
                questionId = question.questionId,
                createdAt = question.createdAt,
                streamId = question.streamId,
                text = question.text,
                media = question.media,
            )
            return liveQuestionsByUserRepository.save(questionsByUser)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
