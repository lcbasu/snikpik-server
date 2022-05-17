package com.server.ud.entities.live_question

import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("live_questions_by_stream")
class LiveQuestionsByStream (

    @PrimaryKeyColumn(name = "stream_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var streamId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "question_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var questionId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("likes_count")
    var likesCount: Long,

    @Column
    var text: String? = null,

    @Column
    var media: String? = null // MediaDetailsV2
)

fun LiveQuestionsByStream.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}
