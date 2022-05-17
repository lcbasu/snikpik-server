package com.server.ud.entities.live_question

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("live_questions_count_by_stream")
class LiveQuestionsCountByStream {

    @PrimaryKeyColumn(name = "stream_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var streamId: String? = null

    @Column("questions_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var questionsCount: Long? = null
}

