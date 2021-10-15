package com.dukaankhata.server.controller

import EntityInteractionRequest
import SavedEntityTrackingResponse
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.service.TrackingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 *
 * APIs that are serving data that can be Public for SEO
 *
 * */
@RestController
@RequestMapping("tracking")
class TrackingController {

    @Autowired
    private lateinit var trackingService: TrackingService

    @Autowired
    private lateinit var authProvider: AuthProvider

    @RequestMapping(value = ["/trackEntityInteraction"], method = [RequestMethod.POST])
    fun trackEntityInteraction(@RequestBody request: EntityInteractionRequest): SavedEntityTrackingResponse {
        authProvider.makeSureThePublicRequestHasUserEntity()
        return trackingService.trackEntityInteraction(request)
    }
}
