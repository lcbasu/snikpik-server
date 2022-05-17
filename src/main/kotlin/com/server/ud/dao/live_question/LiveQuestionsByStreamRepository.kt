package com.server.ud.dao.live_question

import com.server.ud.entities.live_question.LiveQuestionsByStream
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface LiveQuestionsByStreamRepository : CassandraRepository<LiveQuestionsByStream?, String?> {
    fun findAllByStreamId(streamId: String, pageable: Pageable): Slice<LiveQuestionsByStream>
    fun deleteAllByStreamId(streamId: String)
    fun deleteAllByStreamIdAndCreatedAtAndQuestionId(streamId: String, createdAt: Instant, commentId: String)
}
