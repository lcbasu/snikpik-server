package com.server.dk.service

import EntityInteractionRequest
import SavedEntityTrackingResponse

abstract class TrackingService {
    abstract fun trackEntityInteraction(request: EntityInteractionRequest): SavedEntityTrackingResponse
}
