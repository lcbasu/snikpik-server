package com.server.ud.dao.live_stream

import com.server.ud.entities.live_stream.LiveStream
import com.server.ud.enums.LiveStreamPlatform
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface LiveStreamRepository : CassandraRepository<LiveStream?, String?> {
    fun findAllByStreamPlatformAndStreamId(streamPlatform: LiveStreamPlatform, streamId: String): List<LiveStream>
    fun findAllByStreamPlatform(streamPlatform: LiveStreamPlatform, pageable: Pageable): Slice<LiveStream>
}
