package com.server.ud.dao.live_stream

import com.server.ud.entities.live_stream.LiveStream
import com.server.ud.entities.live_stream.LiveStreamSubscribedByUser
import com.server.ud.entities.live_stream.SubscribedLiveStreamUsersByStream
import com.server.ud.entities.live_stream.SubscribedLiveStreamsByUser
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

@Repository
interface LiveStreamSubscribedByUserRepository : CassandraRepository<LiveStreamSubscribedByUser?, String?> {
    fun findAllByStreamIdAndSubscriberUserId(streamId: String, subscriberUserId: String): List<LiveStreamSubscribedByUser>
}

@Repository
interface SubscribedLiveStreamsByUserRepository : CassandraRepository<SubscribedLiveStreamsByUser?, String?> {
    fun findAllBySubscriberUserId(subscriberUserId: String, pageable: Pageable): Slice<SubscribedLiveStreamsByUser>
}
@Repository
interface SubscribedLiveStreamUsersByStreamRepository : CassandraRepository<SubscribedLiveStreamUsersByStream?, String?> {
}
