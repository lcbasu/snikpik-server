package com.server.ud.service.live_stream

import com.server.ud.dto.AllActiveLiveStreamsRequestResponse
import com.server.ud.dto.GetAllActiveLiveStreamsRequest
import com.server.ud.dto.SaveLiveStreamRequest
import com.server.ud.dto.SavedLiveStreamResponse

abstract class LiveStreamService {
    abstract fun save(request: SaveLiveStreamRequest): SavedLiveStreamResponse?
    abstract fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsRequestResponse?
    abstract fun get(streamId: String): SavedLiveStreamResponse?

}
