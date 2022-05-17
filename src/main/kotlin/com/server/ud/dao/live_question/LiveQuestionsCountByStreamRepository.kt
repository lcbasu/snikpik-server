package com.server.ud.dao.live_question

import com.server.ud.entities.live_question.LiveQuestionsCountByStream
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LiveQuestionsCountByStreamRepository : CassandraRepository<LiveQuestionsCountByStream?, String?> {
    fun findAllByStreamId(streamId: String?): List<LiveQuestionsCountByStream>
    @Query("UPDATE live_questions_count_by_stream SET questions_count = questions_count + 1 WHERE stream_id = ?0")
    fun incrementQuestionCount(streamId: String)
    @Query("UPDATE live_questions_count_by_stream SET questions_count = questions_count - 1 WHERE stream_id = ?0")
    fun decrementQuestionCount(streamId: String)
    fun deleteAllByStreamId(streamId: String)
}
