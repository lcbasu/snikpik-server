package com.server.ud.dao.live_question

import com.server.ud.entities.live_question.LiveQuestion
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LiveQuestionRepository : CassandraRepository<LiveQuestion?, String?> {
    fun findAllByQuestionId(questionId: String?): List<LiveQuestion>
    fun deleteByQuestionId(questionId: String)
}
