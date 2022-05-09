package com.server.ud.service.live_stream

import com.server.ud.dto.*
import com.server.ud.provider.live_stream.LiveStreamProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LiveStreamServiceImpl : LiveStreamService() {

    @Autowired
    private lateinit var liveStreamProvider: LiveStreamProvider

    override fun save(request: SaveLiveStreamRequest): SavedLiveStreamResponse? {
        return liveStreamProvider.save(request)?.toSavedLiveStreamResponse()
    }

    override fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsRequestResponse? {
        return liveStreamProvider.getAllActiveLiveStreams(request)
    }

    override fun get(streamId: String): SavedLiveStreamResponse? {
        return liveStreamProvider.get(streamId)?.toSavedLiveStreamResponse()
    }

}
