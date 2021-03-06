package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.enums.LiveStreamPlatform
import com.server.ud.service.live_stream.LiveStreamService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/liveStream")
class LiveStreamController {

    @Autowired
    private lateinit var liveStreamService: LiveStreamService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun save(@RequestBody request: SaveLiveStreamRequest): SavedLiveStreamResponse? {
        return liveStreamService.save(request)
    }

    @RequestMapping(value = ["/get"], method = [RequestMethod.GET])
    fun get(@RequestParam streamId: String): SavedLiveStreamResponse? {
        return liveStreamService.get(streamId)
    }

    @RequestMapping(value = ["/checkSubscribed"], method = [RequestMethod.GET])
    fun checkSubscribed(@RequestParam streamId: String): LiveStreamSubscribedResponse? {
        return liveStreamService.checkSubscribed(streamId)
    }

    @RequestMapping(value = ["/like"], method = [RequestMethod.POST])
    fun like(@RequestBody request: LiveStreamLikedRequest) {
        return liveStreamService.like(request)
    }

    @RequestMapping(value = ["/subscribe"], method = [RequestMethod.POST])
    fun subscribe(@RequestBody request: LiveStreamSubscribeRequest): LiveStreamSubscribedResponse {
        return liveStreamService.subscribe(request)
    }

    @RequestMapping(value = ["/getAllActiveLiveStreams"], method = [RequestMethod.GET])
    fun getAllActiveLiveStreams(@RequestParam liveStreamPlatform: LiveStreamPlatform,
                                @RequestParam limit: Int,
                                @RequestParam pagingState: String? = null): AllActiveLiveStreamsResponse? {
        return liveStreamService.getAllActiveLiveStreams(
            GetAllActiveLiveStreamsRequest(
                liveStreamPlatform,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getAllSubscribedLiveStreams"], method = [RequestMethod.GET])
    fun getAllSubscribedLiveStreams(@RequestParam limit: Int,
                                    @RequestParam pagingState: String? = null): AllSubscribedLiveStreamsResponse? {
        return liveStreamService.getAllSubscribedLiveStreams(
            GetAllSubscribedStreamsRequest(
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/streamJoinedOrLeft"], method = [RequestMethod.POST])
    fun streamJoinedOrLeft(@RequestBody request: LiveStreamJoinedOrLeftRequest) {
        return liveStreamService.streamJoinedOrLeft(request)
    }
}
