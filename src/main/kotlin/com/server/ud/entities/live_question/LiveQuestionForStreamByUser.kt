package com.server.ud.entities.live_question

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("live_question_for_stream_by_user")
class LiveQuestionForStreamByUser (

    @PrimaryKeyColumn(name = "stream_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var streamId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    var asked: Boolean = false
)

