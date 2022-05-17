package com.server.ud.dao.live_question

import com.server.ud.entities.live_question.LiveQuestionForStreamByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LiveQuestionForStreamByUserRepository : CassandraRepository<LiveQuestionForStreamByUser?, String?> {
    fun findAllByStreamIdAndUserId(streamId: String, userId: String): List<LiveQuestionForStreamByUser>
    fun deleteAllByStreamIdAndUserId(streamId: String, userId: String)
}
