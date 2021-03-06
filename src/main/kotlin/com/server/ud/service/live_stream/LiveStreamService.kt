package com.server.ud.service.live_stream

import com.server.ud.dto.*

abstract class LiveStreamService {
    abstract fun save(request: SaveLiveStreamRequest): SavedLiveStreamResponse?
    abstract fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsResponse?
    abstract fun get(streamId: String): SavedLiveStreamResponse?
    abstract fun streamJoinedOrLeft(request: LiveStreamJoinedOrLeftRequest)
    abstract fun like(request: LiveStreamLikedRequest)
    abstract fun subscribe(request: LiveStreamSubscribeRequest): LiveStreamSubscribedResponse
    abstract fun getAllSubscribedLiveStreams(request: GetAllSubscribedStreamsRequest): AllSubscribedLiveStreamsResponse?
    abstract fun checkSubscribed(streamId: String): LiveStreamSubscribedResponse?

}
