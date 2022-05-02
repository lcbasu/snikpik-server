package com.server.ud.provider.integration

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.server.common.properties.IntegrationProperties
import com.server.common.provider.SecurityProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.integration.ExternalIngestionPauseInfoRepository
import com.server.ud.dao.integration.InstagramAuthProcessingByUserIdRepository
import com.server.ud.dao.integration.IntegrationAccountInfoByPlatformAccountIdRepository
import com.server.ud.dao.integration.IntegrationAccountInfoByUserIdRepository
import com.server.ud.dao.post.InstagramPostsRepository
import com.server.ud.dto.*
import com.server.ud.entities.integration.common.*
import com.server.ud.entities.post.InstagramPost
import com.server.ud.entities.post.getInstagramPostChildrenResponse
import com.server.ud.enums.*
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.job.UDJobProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.utils.UDKeyBuilder
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class IntegrationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var udJobProvider: UDJobProvider

    @Autowired
    private lateinit var instagramAuthProcessingByUserIdRepository: InstagramAuthProcessingByUserIdRepository

    @Autowired
    private lateinit var integrationAccountInfoByUserIdRepository: IntegrationAccountInfoByUserIdRepository

    @Autowired
    private lateinit var integrationAccountInfoByPlatformAccountIdRepository: IntegrationAccountInfoByPlatformAccountIdRepository

    @Autowired
    private lateinit var externalIngestionPauseInfoRepository: ExternalIngestionPauseInfoRepository

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var integrationProperties: IntegrationProperties

    @Autowired
    private lateinit var instagramPostsRepository: InstagramPostsRepository

    @Autowired
    private lateinit var integrationProvider: IntegrationProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun getIntegrationAccountInfoByUserId(userId: String, platform: IntegrationPlatform, accountId: String): IntegrationAccountInfoByUserId? =
        try {
            val users = integrationAccountInfoByUserIdRepository.findAllByUserIdAndPlatformAndAccountId(userId, platform, accountId)
            if (users.size > 1) {
                error("More than one IntegrationAccountInfoByUserId has same userId: $userId, platform: $platform, accountId: $accountId")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting IntegrationAccountInfoByUserId for userId: $userId, platform: $platform, accountId: $accountId\" failed.")
            e.printStackTrace()
            null
        }

    fun getIntegrationAccountInfoByUserId(userId: String): List<IntegrationAccountInfoByUserId> =
        try {
            integrationAccountInfoByUserIdRepository.findAllByUserId(userId)
        } catch (e: Exception) {
            logger.error("Getting all IntegrationAccountInfoByUserId for userId: $userId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun getAllIntegrationAccountInfoByPlatformAccountId(accountId: String): List<IntegrationAccountInfoByPlatformAccountId> =
        try {
            integrationAccountInfoByPlatformAccountIdRepository.findAllByAccountId(accountId)
        } catch (e: Exception) {
            logger.error("Getting all IntegrationAccountInfoByPlatformAccountId for accountId: $accountId failed.")
            e.printStackTrace()
            emptyList()
        }

    fun saveIntegrationAccountInfoByUserId(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): IntegrationAccountInfoByUserId {
            val result = integrationAccountInfoByUserIdRepository.save(integrationAccountInfoByUserId)
            integrationAccountInfoByPlatformAccountIdRepository.save(result.toIntegrationAccountInfoByPlatformAccountId())
            return result
        }

    fun connect(request: ConnectInstagramAccountRequest): ConnectInstagramAccountResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        if (userDetailsFromToken.getUserIdToUse() != request.userId) {
            error("UserId in token does not match userId in request")
        }
        val instagramAuthProcessingByUserId = instagramAuthProcessingByUserIdRepository.save(
            InstagramAuthProcessingByUserId(
                userId = request.userId,
                code = request.code,
                state = InstagramAuthProcessingState.PROCESSING,
                createdAt = DateUtils.getInstantNow(),
            )
        )
        try {

            // Generate the short-lived token
            val instagramShortLivedAccessTokenResponse = generateShortLivedToken(instagramAuthProcessingByUserId)
                ?: error("Failed to generate short-lived token for userId: ${instagramAuthProcessingByUserId.userId}")
            // VERY-CRITICAL Raise an alert in case of error

            val shortLivedAccessTokenResponse = saveAndGetShortLivedAccessTokenResponse(instagramAuthProcessingByUserId, instagramShortLivedAccessTokenResponse)

            if (shortLivedAccessTokenResponse.error == true || shortLivedAccessTokenResponse.model == null) {
                instagramAuthProcessingByUserIdRepository.save(
                    instagramAuthProcessingByUserId.copy(state = InstagramAuthProcessingState.FAILED)
                )
                return ConnectInstagramAccountResponse(
                    userId = request.userId,
                    integrationAccount = null,
                    error = shortLivedAccessTokenResponse.error,
                    errorMessage = shortLivedAccessTokenResponse.errorMessage,
                )
            }

            // Generate the long-lived token
            val instagramLongLivedAccessTokenResponse = generateLongLivedToken(shortLivedAccessTokenResponse.model)
                ?: error("Failed to generate long-lived token for userId: ${instagramAuthProcessingByUserId.userId}")
            // VERY-CRITICAL Raise an alert in case of error

            // Update the entry for the info about integration
            var integrationAccountInfoByUserId = saveIntegrationAccountInfoByUserId(
                shortLivedAccessTokenResponse.model.copy(
                    longLivedAccessToken = instagramLongLivedAccessTokenResponse.instagramAccessToken,
                    expiresIn = instagramLongLivedAccessTokenResponse.expiresIn,
                )
            )

            val instagramUserInfoResponse = getUserAccountInfo(integrationAccountInfoByUserId)
            if (instagramUserInfoResponse != null) {
                integrationAccountInfoByUserId = saveIntegrationAccountInfoByUserId(
                    integrationAccountInfoByUserId.copy(
                        userIdOnPlatform = instagramUserInfoResponse.id,
                        usernameOnPlatform = instagramUserInfoResponse.username,
                    )
                )
            } else {
                logger.error("Failed to get user info for userId: ${instagramAuthProcessingByUserId.userId}")
            }

            // Ingest all instagram post on first connection
            // This is a time taking process
            // And the user expectation is also that the first connection is slow
            // if they have a huge number of posts

            // Figure out a way to time out if there are a lot of posts
            ingestAllInstagramPostsSync(integrationAccountInfoByUserId)

            scheduleJobs(integrationAccountInfoByUserId)

            instagramAuthProcessingByUserIdRepository.save(
                instagramAuthProcessingByUserId.copy(state = InstagramAuthProcessingState.SUCCESS)
            )
            return integrationAccountInfoByUserId.toConnectInstagramAccountResponse(false, null)
        } catch (e: Exception) {
            logger.error("Error while processNewAccountConnect for userId: ${instagramAuthProcessingByUserId.userId}")
            e.printStackTrace()
            instagramAuthProcessingByUserIdRepository.save(
                instagramAuthProcessingByUserId.copy(state = InstagramAuthProcessingState.FAILED)
            )

            return ConnectInstagramAccountResponse(
                userId = request.userId,
                integrationAccount = null,
                error = true,
                errorMessage = "Error while connecting instagram account",
            )
        }
    }

    fun disconnect(request: DisconnectInstagramAccountRequest): DisconnectInstagramAccountResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        if (userDetailsFromToken.getUserIdToUse() != request.userId) {
            error("UserId in token does not match userId in request")
        }

        val integrationAccountInfoByUserId = getIntegrationAccountInfoByUserId(request.userId, IntegrationPlatform.INSTAGRAM, request.instagramUserId)
            ?: error("Failed to find integrationAccountInfoByUserId for instagramUserId: ${request.instagramUserId}")
        // VERY-CRITICAL Raise an alert in case of error

        // Reset the long-lived token
        val updatedIntegrationAccountInfoByUserId = saveIntegrationAccountInfoByUserId(
            integrationAccountInfoByUserId.copy(
                authorizationCode = null,
                shortLivedAccessToken = null,
                longLivedAccessToken = null,
                expiresIn = null,
            )
        )

        // Un-Schedule the job that was refreshing the token
        udJobProvider.unScheduleRefreshingInstagramLongLivedToken(integrationAccountInfoByUserId)
        udJobProvider.unScheduleInstagramPostIngestion(integrationAccountInfoByUserId)
        return DisconnectInstagramAccountResponse(
            userId = request.userId,
            instagramUserId = request.instagramUserId,
            disconnected = updatedIntegrationAccountInfoByUserId.longLivedAccessToken == null,
        )
    }

    fun startProcessingAfterUserApproval(request: StartInstagramIngestionRequest): StartedInstagramIngestionResponse {
        if (!request.autoIngestFuturePosts) {
            updateIngestionState(
                UpdateIngestionStateRequest(
                    userId = request.userId,
                    accountId = request.accountId,
                    platform = IntegrationPlatform.INSTAGRAM,
                    shouldPause = true
            ))
        }
        startProcessingAfterUserApprovalInternal(request)
        return StartedInstagramIngestionResponse(
            request.userId,
            request.accountId,
            true
        )
    }

    fun ingestAllInstagramPosts(key: String) {
        logger.info("Ingest all instagram posts for key: $key")
        val parsedKeyForIntegrationAccountInfoByUserId = UDKeyBuilder.parseJobKeyForIntegrationAccountInfoByUserId(key)
        val integrationAccountInfoByUserId = integrationProvider.getIntegrationAccountInfoByUserId(parsedKeyForIntegrationAccountInfoByUserId.userId, IntegrationPlatform.valueOf(parsedKeyForIntegrationAccountInfoByUserId.platform), parsedKeyForIntegrationAccountInfoByUserId.accountId)
            ?: error("Failed to find integrationAccountInfoByUserId for key: $key in ingestAllInstagramPosts")
        // VERY-CRITICAL Raise an alert in case of error

        // Start the ingestion
        ingestAllInstagramPostsAsync(integrationAccountInfoByUserId)
        logger.info("Ingest all instagram posts for key: $key completed")
    }

    fun updateIngestionState(request: UpdateIngestionStateRequest): UpdateIngestionStateResponse {
        val integrationAccountInfoByUserId = getIntegrationAccountInfoByUserId(request.userId, request.platform, request.accountId)
            ?: error("Failed to find integrationAccountInfoByUserId for request: $request")
        // VERY-CRITICAL Raise an alert in case of error
        // Update the entry for the info about integration
        val updatedData = saveIntegrationAccountInfoByUserId(
            integrationAccountInfoByUserId.copy(
                pauseIngestion = request.shouldPause,
            )
        )

        val processingDone = handleExternalIngestionPauseInfo(updatedData)

        return UpdateIngestionStateResponse(
            request.userId,
            updatedData.toIntegrationAccountResponse()
        )
    }

    fun refreshInstagramLongLivedToken(key: String) {
        logger.info("Refreshing Instagram long-lived token for key: $key")
        val parsedKeyForIntegrationAccountInfoByUserId = UDKeyBuilder.parseJobKeyForIntegrationAccountInfoByUserId(key)
        val integrationAccountInfoByUserId = getIntegrationAccountInfoByUserId(parsedKeyForIntegrationAccountInfoByUserId.userId, IntegrationPlatform.valueOf(parsedKeyForIntegrationAccountInfoByUserId.platform), parsedKeyForIntegrationAccountInfoByUserId.accountId)
            ?: error("Failed to find integrationAccountInfoByUserId for key: $key")
        // VERY-CRITICAL Raise an alert in case of error

        logger.info("Old long-lived token: ${integrationAccountInfoByUserId.longLivedAccessToken}")

        // Refresh the long-lived token
        val instagramLongLivedAccessTokenResponse = refreshLongLivedToken(integrationAccountInfoByUserId)
            ?: error("Failed to refresh long-lived token for key: $key")
        // VERY-CRITICAL Raise an alert in case of error

        // Update the entry for the info about integration
        saveIntegrationAccountInfoByUserId(
            integrationAccountInfoByUserId.copy(
                longLivedAccessToken = instagramLongLivedAccessTokenResponse.instagramAccessToken,
            )
        )

        logger.info("New long-lived token: ${instagramLongLivedAccessTokenResponse.instagramAccessToken}")

        logger.info("Refreshed Instagram long-lived token for key: $key")
    }

    private fun getAllExternalIngestionPauseInfo(userId: String, platform: IntegrationPlatform, accountId: String): List<ExternalIngestionPauseInfo> {
        return externalIngestionPauseInfoRepository.findAllByUserIdAndPlatformAndAccountId(
            userId,
            platform,
            accountId
        )
    }

//    fun processAllInstagramPosts(userId: String, accountId: String) {
//        GlobalScope.launch {
//            val instagramPosts = getAllInstagramPost(userId, accountId)
//            instagramPosts.chunked(10).map {
//                async {
//                    it.map {
//                        async {
//                            processSavedInstagramPost(it)
//                        }
//                    }.map {
//                        it.await()
//                    }
//                }
//            }.map {
//                it.await()
//            }
//        }
//    }

    private fun startProcessingAfterUserApprovalInternal(request: StartInstagramIngestionRequest) {
        GlobalScope.launch {
            request.postIdsNotWanted.mapNotNull {
                getInstagramPost(request.userId, request.accountId, it)
            }.map {
                saveInstagramPost(it.copy(state = InstagramPostProcessingState.DOES_NOT_WANT_TO_INGEST))
            }
            // Do not process now. Just schedule the processing job
//            processAllInstagramPosts(userId = request.userId, accountId = request.accountId)

            val integrationAccountInfoByUserId = getIntegrationAccountInfoByUserId(request.userId, IntegrationPlatform.INSTAGRAM, request.accountId)
            integrationAccountInfoByUserId?.let {
                logger.info("Starting Instagram ingestion for userId: ${request.userId}, accountId: ${request.accountId}")
                saveIntegrationAccountInfoByUserId(
                    it.copy(firstIngestionDone = true)
                )
                ingestAllInstagramPostsAsync(integrationAccountInfoByUserId)
            }
        }
    }

    fun saveInstagramPost(instagramPost: InstagramPost): InstagramPost {
        return instagramPostsRepository.save(instagramPost)
    }

    fun scheduleJobs(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId) {
        // Refresh the token after 75% of the time is over
        val expireInSecondsForJob = ((integrationAccountInfoByUserId.expiresIn ?: DateUtils.convertDaysToSeconds(30)) * 0.75).toLong()

        // Schedule a job to refresh the long-lived token
        udJobProvider.scheduleRefreshingInstagramLongLivedToken(integrationAccountInfoByUserId, expireInSecondsForJob)

        // Schedule a job to ingest the data every 6 hours
        // But also run this job immediately so that we have data to show to use for him to be able to select or deselect any post
        udJobProvider.scheduleInstagramPostIngestion(integrationAccountInfoByUserId, DateUtils.convertHoursToSeconds(6))
    }

    private fun saveAndGetShortLivedAccessTokenResponse(instagramAuthProcessingByUserId: InstagramAuthProcessingByUserId, instagramShortLivedAccessTokenResponse: InstagramShortLivedAccessTokenResponse): ShortLivedAccessTokenResponse {

        val allExistingAccountInfo = getAllIntegrationAccountInfoByPlatformAccountId(instagramShortLivedAccessTokenResponse.instagramUserId)
        val instagramAccounts = allExistingAccountInfo.filter { it.platform == IntegrationPlatform.INSTAGRAM }
        if (instagramAccounts.isNotEmpty()) {
            val userIds = instagramAccounts.map { it.userId }

            if (userIds.contains(instagramAuthProcessingByUserId.userId).not()) {
                logger.error("Account Id: ${instagramShortLivedAccessTokenResponse.instagramUserId} is already in use by another userId: ${instagramAuthProcessingByUserId.userId}")
                return ShortLivedAccessTokenResponse(
                    model = null,
                    error = true,
                    errorMessage = "Account Id: ${instagramShortLivedAccessTokenResponse.instagramUserId} is already in use by another userId: ${instagramAuthProcessingByUserId.userId}",
                )
            }
        }

        // Save an entry for the info about integration
        val result = saveIntegrationAccountInfoByUserId(
            IntegrationAccountInfoByUserId(
                userId = instagramAuthProcessingByUserId.userId,
                platform = IntegrationPlatform.INSTAGRAM,
                accountId = instagramShortLivedAccessTokenResponse.instagramUserId,
                createdAt = DateUtils.getInstantNow(),
                syncType = IntegrationPlatformSyncType.ALL,
                shortLivedAccessToken = instagramShortLivedAccessTokenResponse.instagramAccessToken,
                authorizationCode = instagramAuthProcessingByUserId.code,
            )
        )

        return ShortLivedAccessTokenResponse(
            model = result,
            error = false,
            errorMessage = "Account Id: ${instagramShortLivedAccessTokenResponse.instagramUserId} is already in use by another userId: ${instagramAuthProcessingByUserId.userId}",
        )
    }

    private fun generateShortLivedToken(instagramAuthProcessingByUserId: InstagramAuthProcessingByUserId): InstagramShortLivedAccessTokenResponse? {
        return try {

//
//
//            val uri = "https://api.instagram.com/oauth/access_token"
//
//            val restTemplate = RestTemplate()
//            val headers = HttpHeaders()
//            headers.setContentType(MediaType.APPLICATION_JSON)
//            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
////            map.add("id", "1")
//            map.add("client_id", integrationProperties.instagram.appId)
//            map.add("client_secret", integrationProperties.instagram.appSecret)
//            map.add("code", instagramAuthProcessingByUserId.code)
//            map.add("grant_type", "authorization_code")
//            map.add("redirect_uri", integrationProperties.instagram.redirectUri)
//            val request: HttpEntity<MultiValueMap<String, String>> = HttpEntity(map, headers)
//            val restResponse: ResponseEntity<String> = restTemplate.postForEntity(
//                uri, request,
//                String::class.java
//            )
////            Assertions.assertEquals(response.statusCode, HttpStatus.CREATED)
//
//            logger.info("Response: ${restResponse.body}")

            val body = JSONObject()
            body.append("client_id", integrationProperties.instagram.appId)
            body.append("client_secret", integrationProperties.instagram.appSecret)
            body.append("code", instagramAuthProcessingByUserId.code)
            body.append("grant_type", "authorization_code")
            body.append("redirect_uri", integrationProperties.instagram.redirectUri)
            val response: HttpResponse<JsonNode> = Unirest
                .post("https://api.instagram.com/oauth/access_token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", integrationProperties.instagram.appId)
                .field("client_secret", integrationProperties.instagram.appSecret)
                .field("code", instagramAuthProcessingByUserId.code)
                .field("grant_type", "authorization_code")
                .field("redirect_uri", integrationProperties.instagram.redirectUri)
                .asJson()
            logger.info("Instagram short-lived token response: ${response.toString()}")
            if (response.status == 200 && response.body.`object`.has("access_token") && response.body.`object`.has("user_id")) {
                InstagramShortLivedAccessTokenResponse(
                    instagramAccessToken = response.body.`object`.getString("access_token"),
                    instagramUserId = response.body.`object`.get("user_id").toString(),
                )
            } else {
                logger.error("Failed to generate short-lived token for userId: ${instagramAuthProcessingByUserId.userId}")
                null
            }
        } catch (e: Exception) {
            logger.error("Error while generate short-lived token for userId: ${instagramAuthProcessingByUserId.userId}")
            e.printStackTrace()
            null
        } ?: error("Failed to generate short-lived token for userId: ${instagramAuthProcessingByUserId.userId}")
    }

    private fun generateLongLivedToken(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): InstagramLongLivedAccessTokenResponse? {
        return try {
            val response: HttpResponse<JsonNode> = Unirest
                .get("https://graph.instagram.com/access_token?client_secret=${integrationProperties.instagram.appSecret}&grant_type=ig_exchange_token&access_token=${integrationAccountInfoByUserId.shortLivedAccessToken}")
                .header("Content-Type", "application/x-www-form-urlencoded")
//                .routeParam("client_secret", integrationProperties.instagram.appSecret)
//                .routeParam("grant_type", "ig_exchange_token")
//                //Here we need to send Short lived token as this is the first time we are generating the token
//                .routeParam("access_token", integrationAccountInfoByUserId.shortLivedAccessToken)
                .asJson()
            parseLongLivedTokenResponse(response)
        } catch (e: Exception) {
            logger.error("Error while generate long-lived token for userId: ${integrationAccountInfoByUserId.userId}")
            e.printStackTrace()
            null
        } ?: error("Failed to generate long-lived token for userId: ${integrationAccountInfoByUserId.userId}")
    }

    private fun refreshLongLivedToken(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): InstagramLongLivedAccessTokenResponse? {
        return try {
            val response: HttpResponse<JsonNode> = Unirest
                .get("https://graph.instagram.com/refresh_access_token?grant_type=ig_refresh_token&access_token=${integrationAccountInfoByUserId.longLivedAccessToken}")
                .header("Content-Type", "application/x-www-form-urlencoded")
//                .routeParam("grant_type", "ig_refresh_token")
//                //Here we need to send Long lived token
//                .routeParam("access_token", integrationAccountInfoByUserId.longLivedAccessToken)
                .asJson()
            parseLongLivedTokenResponse(response)
        } catch (e: Exception) {
            logger.error("Error while generate long-lived token for userId: ${integrationAccountInfoByUserId.userId}")
            e.printStackTrace()
            null
        } ?: error("Failed to generate long-lived token for userId: ${integrationAccountInfoByUserId.userId}")
    }

    private fun parseLongLivedTokenResponse(response: HttpResponse<JsonNode>): InstagramLongLivedAccessTokenResponse? {
        return if (response.status == 200 && response.body.`object`.has("access_token")) {
            InstagramLongLivedAccessTokenResponse(
                instagramAccessToken = response.body.`object`.getString("access_token"),
                tokenType = response.body.`object`.getString("token_type"),
                expiresIn = response.body.`object`.getLong("expires_in"),
            )
        } else {
            logger.error("Failed to parse long-lived token response: ${response.toString()}")
            null
        }
    }

    private fun handleExternalIngestionPauseInfo(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): Boolean {
        val entries = getAllExternalIngestionPauseInfo(
            integrationAccountInfoByUserId.userId,
            integrationAccountInfoByUserId.platform,
            integrationAccountInfoByUserId.accountId
        )

        val lastEntries = entries.filter { it.pauseEndAt == null }
        // lastEntries should be empty or have only one entry
        if (lastEntries.size > 1) {
            error("Multiple entries found for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId} in ExternalIngestionPauseInfo")
        } else {
            logger.info("lastEntries is good to process with size: ${lastEntries.size} and total entries: ${entries.size} for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId} in ExternalIngestionPauseInfo")
        }
        val lastEntry = lastEntries.firstOrNull()
        if (integrationAccountInfoByUserId.pauseIngestion) {
            if (lastEntry != null) {
                // Means we have an entry for which end date is null
                // Do nothing as we are trying to pause already paused entry
                logger.error("endAt in ExternalIngestionPauseInfo should have been set to non-null when we were setting the last value entries found for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId}")
                logger.error("Since we did not, we will have to update the last entry with startAt set to current time and save that entry")
            } else {
                // Pause
                // Create a new entry if this is the first time or if we have already paused and un-paused in the past
                val pauseEntry = ExternalIngestionPauseInfo(
                    userId = integrationAccountInfoByUserId.userId,
                    platform = integrationAccountInfoByUserId.platform,
                    accountId = integrationAccountInfoByUserId.accountId,
                    pauseStartAt = DateUtils.getInstantNow(),
                    pauseEndAt = null
                )
                externalIngestionPauseInfoRepository.save(pauseEntry)
            }
        } else {
            if (lastEntry != null) {
                // Update the last entry to mark the end of last pause session
                externalIngestionPauseInfoRepository.save(lastEntry.copy(
                    pauseEndAt = DateUtils.getInstantNow()
                ))
            } else {
                // Do nothing as we are trying to unpause already unpaused entry
                logger.error("We are trying to unpause without having to have paused in the past for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId}")
                logger.info("Do nothing")
            }
        }

        return true
    }

    private fun ingestAllInstagramPostsAsync(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId) {
        GlobalScope.launch {
            ingestAllInstagramPostsSync(integrationAccountInfoByUserId)
        }
    }

    private fun ingestAllInstagramPostsSync(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId) {
        runBlocking {
            logger.info("Starting to ingest all instagram posts for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId}")
            val posts = getAllPostsFromInstagram(integrationAccountInfoByUserId)
            val allBlockingTimeSession = getAllBlockingTimeSession(userId = integrationAccountInfoByUserId.userId, platform = integrationAccountInfoByUserId.platform, accountId = integrationAccountInfoByUserId.accountId)
            posts.chunked(5).map {
                async {
                    it.map {
                        async {
                            try {
                                // Adding try catch to avoid braking if anything in between breaks
                                val savedPost = save(integrationAccountInfoByUserId, it)
                                if (integrationAccountInfoByUserId.firstIngestionDone) {
                                    // Make sure that the first time ingestion flag is active
                                    // This is to make sure that we took user inputs like manual
                                    // deselction and auto uploads for future posts
                                    // Before processing the saved instagram posts
                                    savedPost?.let {
                                        processSavedInstagramPost(savedPost, allBlockingTimeSession)
                                    }
                                } else {
                                    logger.warn("First ingestion is not done yet for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId}")
                                }
                            } catch (e: Exception) {
                                logger.error("Saving post failed: ${it.id}")
                            }
                        }
                    }.map { it.await() }
                }
            }.map {
                it.await()
            }
            logger.info("Finished ingesting all instagram posts for userId: ${integrationAccountInfoByUserId.userId}, platform: ${integrationAccountInfoByUserId.platform}, accountId: ${integrationAccountInfoByUserId.accountId}")
        }
    }

    private fun getInstagramPost(userId: String, accountId: String, postId: String): InstagramPost? =
        try {
            val posts = instagramPostsRepository.findAllByUserIdAndAccountIdAndPostId(userId, accountId, postId)
            if (posts.size > 1) {
                error("More than one post has same postId: $postId")
            }
            posts.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting InstagramPost for $postId failed.")
            e.printStackTrace()
            null
        }

//    private fun getAllInstagramPost(userId: String, accountId: String): List<InstagramPost> =
//        try {
//            instagramPostsRepository.findAllByUserIdAndAccountId(userId, accountId)
//        } catch (e: Exception) {
//            logger.error("Getting InstagramPosts for $userId failed.")
//            e.printStackTrace()
//            emptyList()
//        }

    private fun save(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId, response: InstagramPostResponse) : InstagramPost? {
        try {
            logger.info("Saving InstagramPost for ${response.id}")
            val existing = getInstagramPost(integrationAccountInfoByUserId.userId, integrationAccountInfoByUserId.accountId, response.id)
            existing?.let {
                logger.warn("Trying to save existing post: ${response.id}. So returning the already saved one.")
                println(it)
                return it
            }

            var childrenResponse: InstagramPostChildrenResponse? = null
            if (response.mediaType == InstagramMediaType.CAROUSEL_ALBUM) {
                val token = integrationAccountInfoByUserId.longLivedAccessToken
                if (token.isNullOrBlank()) {
                    logger.error("No longLivedAccessToken found for userId: ${integrationAccountInfoByUserId.userId}")
                } else {
                    childrenResponse = getInstagramChildrenResponse(response.id, token)
                }
            }

            val instagramPost = InstagramPost(
                userId = integrationAccountInfoByUserId.userId,
                accountId = integrationAccountInfoByUserId.accountId,
                postId = response.id,
                state = InstagramPostProcessingState.WANT_TO_INGEST,
                createdAt = response.timestamp?.let { DateUtils.parseISODateTime(it) } ?: DateUtils.getInstantNow(),
                caption = response.caption,
                mediaType = response.mediaType,
                mediaUrl = response.mediaUrl,
                thumbnailUrl = response.thumbnailUrl,
                permalink = response.permalink,
                timestamp = response.timestamp,
                username = response.username,
                children = childrenResponse.convertToString(),
            )
            logger.info("Saving InstagramPost for ${response.id}")
            return saveInstagramPost(instagramPost)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getIsPostWithinBlockedTime(instagramPost: InstagramPost, allBlockingTimeSession: List<ExternalIngestionPauseInfo>): Boolean {
        var result = false
        for (externalIngestionPauseInfo in allBlockingTimeSession) {
            if (instagramPost.createdAt.isAfter(externalIngestionPauseInfo.pauseStartAt) &&
                instagramPost.createdAt.isBefore(externalIngestionPauseInfo.pauseEndAt)) {
                result = true
                break
            }
        }
        return result
    }

    private fun getAllBlockingTimeSession(userId: String, platform: IntegrationPlatform, accountId: String): List<ExternalIngestionPauseInfo> {
        val allEntries = getAllExternalIngestionPauseInfo(userId = userId, platform = platform, accountId = accountId)
        val allBlockingTimeSession = mutableListOf<ExternalIngestionPauseInfo>()
        allEntries.forEach {
            if (it.pauseEndAt == null) {
                allBlockingTimeSession.add(it.copy(pauseEndAt = DateUtils.getInstantNow()))
            } else {
                allBlockingTimeSession.add(it)
            }
        }
        return allBlockingTimeSession
    }

    private fun processSavedInstagramPost(instagramPost: InstagramPost, allBlockingTimeSession: List<ExternalIngestionPauseInfo>) {
        GlobalScope.launch {
            try {
                logger.info("Processing InstagramPost for ${instagramPost.postId}")
                val isPostWithinBlockedTime = getIsPostWithinBlockedTime(instagramPost, allBlockingTimeSession)
                if (isPostWithinBlockedTime) {
                    logger.error("CurrentState: ${instagramPost.state}. The instagram post was created during the blocked time so do not process this InstagramPost ${instagramPost.postId} for Unbox: ${instagramPost.state}")
                    saveInstagramPost(instagramPost.copy(state = InstagramPostProcessingState.BLOCKED_FOR_INGESTION))
                    return@launch
                }

                when (instagramPost.state) {
                    InstagramPostProcessingState.BLOCKED_FOR_INGESTION -> {
                        logger.error("The instagram post was created during the blocked time so do not process this InstagramPost ${instagramPost.postId} for Unbox: ${instagramPost.state}")
                    }
                    InstagramPostProcessingState.DOES_NOT_WANT_TO_INGEST -> {
                        logger.error("The instagram user does not want this InstagramPost ${instagramPost.postId} to be present on Unbox: ${instagramPost.state}")
                    }
                    InstagramPostProcessingState.PROCESSING -> {
                        logger.error("Post is already being processed and in state: ${instagramPost.state}")
                    }
                    InstagramPostProcessingState.FAILED_RETRY -> {
                        logger.error("Post: ${instagramPost.postId} failed when we retried. So no need to process again")
                    }
                    InstagramPostProcessingState.SUCCESS -> {
                        logger.info("Post: ${instagramPost.postId} is already successfully processed. Do nothing.")
                    }
                    InstagramPostProcessingState.NOT_SUPPORTED -> {
                        logger.error("Post: ${instagramPost.postId} is not supported. May be in future.")
                    }
                    InstagramPostProcessingState.WANT_TO_INGEST,
                    InstagramPostProcessingState.FAILED -> {
                        logger.info("Try processing InstagramPost: ${instagramPost.postId}.")
                        // Let us process for the first time or retry failed post again
                        val mediaTypes = instagramPost.getInstagramPostChildrenResponse().data.map { it.mediaType.name }.toSet()
                        // We only support carousel with only images
                        // 2 cases that are not supported are
                        // Case 1: Carousel with both image and video
                        // Case 2: All Video carousel
                        if (
                        // Case 1
                            mediaTypes.size > 1 ||

                            // Case 2
                            (mediaTypes.size == 1 && mediaTypes.first() == InstagramMediaType.VIDEO.name)) {
                            logger.error("Post is not supported: $mediaTypes")
                            instagramPost.state = InstagramPostProcessingState.NOT_SUPPORTED
                            saveInstagramPost(instagramPost)
                            return@launch
                        }

                        // Create Unbox Post from Instagram Post
                        val wasRetrying = instagramPost.state == InstagramPostProcessingState.FAILED
                        instagramPost.state = InstagramPostProcessingState.PROCESSING
                        var updatedInstagramPost = saveInstagramPost(instagramPost)
                        val unboxPost = postProvider.createPost(updatedInstagramPost)

                        if (unboxPost == null) {
                            logger.error("Unbox Post creation failed for InstagramPost: ${instagramPost.postId}")
                            instagramPost.state = if (wasRetrying) InstagramPostProcessingState.FAILED_RETRY else InstagramPostProcessingState.FAILED
                            updatedInstagramPost = saveInstagramPost(instagramPost)
                            // Send for retry
                            logger.warn("Sending for retrying Unbox Post creation for InstagramPost: ${instagramPost.postId}")
                            processSavedInstagramPost(updatedInstagramPost, allBlockingTimeSession)
                        } else {
                            logger.info("Unbox Post creation successful for InstagramPost: ${instagramPost.postId}")
                            instagramPost.state = InstagramPostProcessingState.SUCCESS
                            saveInstagramPost(instagramPost)
                        }
                    }
                }
                logger.info("Processing InstagramPost: ${instagramPost.postId} completed.")
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("InstagramPost processing failed for postId: ${instagramPost.postId}")
            }
        }
    }

    private fun getAllPostsFromInstagram(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): List<InstagramPostResponse> {
        logger.info("Getting all posts from Instagram for userId: ${integrationAccountInfoByUserId.userId}")
        val result = mutableListOf<InstagramPostResponse>()
        val accountId = integrationAccountInfoByUserId.accountId
        val token = integrationAccountInfoByUserId.longLivedAccessToken ?: error("Missing longLivedAccessToken for userId: ${integrationAccountInfoByUserId.userId}")
        val startupLink = "https://graph.instagram.com/v12.0/${accountId}/media?access_token=${token}&pretty=1&fields=id,caption,media_type,media_url,permalink,thumbnail_url,timestamp,username"
        var response = getInstagramPostsPaginatedResponse(startupLink)
        if (response == null) {
            // CRITICAL: Raise alert if we get an error
            logger.error("Error while getting data for startupLink: $startupLink")
            return result
        } else {
            // Keep getting the data until the response has no next pagination
            result.addAll(response.data)
            while (response?.paging?.next != null) {
                val pageSize = 25
                val after = response.paging?.cursors?.after
                val nextUrl = "$startupLink&limit=$pageSize&after=$after"
                response = getInstagramPostsPaginatedResponse(nextUrl)
                if (response != null) {
                    result.addAll(response.data)
                }
            }
        }
        // Do this after the first user approval is over
//        saveIntegrationAccountInfoByUserIdRepository(
//            integrationAccountInfoByUserId.copy(firstIngestionDone = true)
//        )
        logger.info("Successfully retrieved all posts from Instagram for userId: ${integrationAccountInfoByUserId.userId}, total posts: ${result.size}")
        return result
    }

    private fun getUserAccountInfo(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): InstagramUserInfoResponse? {
        logger.info("Getting user info from instagram userId: ${integrationAccountInfoByUserId.userId}")
        val accountId = integrationAccountInfoByUserId.accountId
        val token = integrationAccountInfoByUserId.longLivedAccessToken ?: error("Missing longLivedAccessToken for userId: ${integrationAccountInfoByUserId.userId}")
        return try {
            val uri = "https://graph.instagram.com/v13.0/${accountId}?fields=id,username,account_type&access_token=${token}"
            val restTemplate = RestTemplate()
            restTemplate.getForObject(uri, InstagramUserInfoResponse::class.java) ?: error("Error while getting user info for accountId: $accountId")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while getting user info for accountId: $accountId")
            null
        }
    }

    private fun getInstagramPostsPaginatedResponse(uri: String): InstagramPostsPaginatedResponse? {
        return try {
            val restTemplate = RestTemplate()
            restTemplate.getForObject(uri, InstagramPostsPaginatedResponse::class.java) ?: error("Error while getting data for uri: $uri")
        } catch (e: Exception) {
            // CRITICAL: Raise alert if we get an error
            e.printStackTrace()
            logger.error("Error while getting data for uri: $uri")
            null
        }
    }

    private fun getInstagramChildrenResponse(postId: String, token: String): InstagramPostChildrenResponse? {
        return try {
            val uri = "https://graph.instagram.com/${postId}/children?access_token=${token}&fields=id,media_type,media_url,permalink,thumbnail_url,timestamp,username"
            val restTemplate = RestTemplate()
            restTemplate.getForObject(uri, InstagramPostChildrenResponse::class.java) ?: error("Error while getting children data for postId: $postId")
        } catch (e: Exception) {
            // CRITICAL: Raise alert if we get an error
            e.printStackTrace()
            logger.error("Error while getting children data for postId: $postId")
            null
        }
    }

    fun getInstagramPosts(request: GetInstagramPostsRequest): AllInstagramPostsResponse {
        val result = getInstagramPostsByUser(request)
        return AllInstagramPostsResponse(
            posts = result.content?.filterNotNull()?.map { it.toSavedInstagramPostResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun getInstagramPostsByUser(request: GetInstagramPostsRequest): CassandraPageV2<InstagramPost> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val posts = instagramPostsRepository.findAllByUserIdAndAccountId(request.userId, request.accountId, pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

    fun getAllIntegrationAccountsForUser(): AllIntegrationAccountsResponse? {
        val userDetailsFromToken = securityProvider.validateRequest()
        return AllIntegrationAccountsResponse(
            accounts = getIntegrationAccountInfoByUserId(userDetailsFromToken.getUserIdToUse()).map { it.toIntegrationAccountResponse() }
        )
    }

}
