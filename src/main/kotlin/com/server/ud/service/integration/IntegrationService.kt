package com.server.ud.service.integration

import com.server.ud.dto.*

abstract class IntegrationService {
    abstract fun connect(request: ConnectInstagramAccountRequest): ConnectInstagramAccountResponse
    abstract fun disconnect(request: DisconnectInstagramAccountRequest): DisconnectInstagramAccountResponse
    abstract fun startProcessingAfterUserApproval(request: StartInstagramIngestionRequest): StartedInstagramIngestionResponse
    abstract fun updateIngestionState(request: UpdateIngestionStateRequest): UpdateIngestionStateResponse
}
