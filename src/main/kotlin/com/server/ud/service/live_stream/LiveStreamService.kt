package com.server.ud.service.live_stream

import com.server.ud.dto.*

abstract class LiveStreamService {
    abstract fun save(request: SaveLiveStreamRequest): SavedLiveStreamResponse?
    abstract fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsRequestResponse?
    abstract fun get(streamId: String): SavedLiveStreamResponse?
    abstract fun streamJoinedOrLeft(request: LiveStreamJoinedOrLeftRequest)
    abstract fun like(request: LiveStreamLikedRequest)

}
