package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.integration.IntegrationService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/integration/instagram")
class InstagramIntegrationController {

    @Autowired
    private lateinit var integrationService: IntegrationService

    @RequestMapping(value = ["/connect"], method = [RequestMethod.POST])
    fun connect(@RequestBody request: ConnectInstagramAccountRequest): ConnectInstagramAccountResponse {
        return integrationService.connect(request)
    }

    @RequestMapping(value = ["/disconnect"], method = [RequestMethod.POST])
    fun disconnect(@RequestBody request: DisconnectInstagramAccountRequest): DisconnectInstagramAccountResponse {
        return integrationService.disconnect(request)
    }

    @RequestMapping(value = ["/startProcessingAfterUserApproval"], method = [RequestMethod.POST])
    fun startProcessingAfterUserApproval(@RequestBody request: StartInstagramIngestionRequest): StartedInstagramIngestionResponse {
        return integrationService.startProcessingAfterUserApproval(request)
    }

    @RequestMapping(value = ["/updateIngestionState"], method = [RequestMethod.POST])
    fun updateIngestionState(@RequestBody request: UpdateIngestionStateRequest): UpdateIngestionStateResponse {
        return integrationService.updateIngestionState(request)
    }

    @RequestMapping(value = ["/getInstagramPosts"], method = [RequestMethod.GET])
    fun getInstagramPosts(@RequestParam userId: String,
                          @RequestParam accountId: String,
                          @RequestParam limit: Int,
                          @RequestParam pagingState: String?): AllInstagramPostsResponse {
        return integrationService.getInstagramPosts(
            GetInstagramPostsRequest(
                userId,
                accountId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getAllIntegrationAccountsForUser"], method = [RequestMethod.GET])
    fun getAllIntegrationAccountsForUser(): AllIntegrationAccountsResponse? {
        return integrationService.getAllIntegrationAccountsForUser()
    }
}
