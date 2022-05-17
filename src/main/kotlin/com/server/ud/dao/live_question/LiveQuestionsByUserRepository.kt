package com.server.ud.dao.live_question

import com.server.ud.entities.live_question.LiveQuestionsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface LiveQuestionsByUserRepository : CassandraRepository<LiveQuestionsByUser?, String?> {
    fun findAllByUserId(userId: String): List<LiveQuestionsByUser>
    fun findAllByUserIdAndCreatedAtAndQuestionId(userId: String, createdAt: Instant, questionId: String): List<LiveQuestionsByUser>
}
