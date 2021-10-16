package com.server.dk.service.impl

import EntityInteractionRequest
import SavedEntityTrackingResponse
import com.server.dk.provider.EntityTrackingProvider
import com.server.dk.service.TrackingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import toSavedEntityTrackingResponse

@Service
class TrackingServiceImpl : TrackingService() {

    @Autowired
    private lateinit var entityTrackingProvider: EntityTrackingProvider

    override fun trackEntityInteraction(request: EntityInteractionRequest): SavedEntityTrackingResponse {
        return entityTrackingProvider.saveProduct(request)?.toSavedEntityTrackingResponse() ?: error("Error while saving request: ${request.toString()}")
    }

}
