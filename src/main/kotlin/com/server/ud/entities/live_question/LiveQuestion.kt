package com.server.ud.entities.live_question

import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("live_questions")
data class LiveQuestion (

    @PrimaryKeyColumn(name = "question_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var questionId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("stream_id")
    var streamId: String,

    @Column("user_id")
    var userId: String,

    @Column
    var text: String? = null,

    @Column
    var media: String? = null // MediaDetailsV2
)

fun LiveQuestion.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}
