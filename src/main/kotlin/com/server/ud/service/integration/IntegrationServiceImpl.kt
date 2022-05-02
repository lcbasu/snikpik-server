package com.server.ud.service.integration

import com.server.ud.dto.*
import com.server.ud.provider.integration.IntegrationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IntegrationServiceImpl : IntegrationService() {

    @Autowired
    private lateinit var integrationProvider: IntegrationProvider

    override fun connect(request: ConnectInstagramAccountRequest): ConnectInstagramAccountResponse {
        return integrationProvider.connect(request)
    }

    override fun disconnect(request: DisconnectInstagramAccountRequest): DisconnectInstagramAccountResponse {
        return integrationProvider.disconnect(request)
    }

    override fun startProcessingAfterUserApproval(request: StartInstagramIngestionRequest): StartedInstagramIngestionResponse {
        return integrationProvider.startProcessingAfterUserApproval(request)
    }

    override fun updateIngestionState(request: UpdateIngestionStateRequest): UpdateIngestionStateResponse {
        return integrationProvider.updateIngestionState(request)
    }

    override fun getInstagramPosts(request: GetInstagramPostsRequest): AllInstagramPostsResponse {
        return integrationProvider.getInstagramPosts(request)
    }

    override fun getAllIntegrationAccountsForUser(): AllIntegrationAccountsResponse? {
        return integrationProvider.getAllIntegrationAccountsForUser()
    }

}
