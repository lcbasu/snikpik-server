package com.server.ud.provider.live_stream

import com.google.firebase.cloud.FirestoreClient
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.server.common.client.AgoraClient
import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.properties.AgoraProperties
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.live_stream.LiveStreamRepository
import com.server.ud.dto.*
import com.server.ud.entities.live_stream.LiveStream
import com.server.ud.enums.LiveStreamJoinStatus
import com.server.ud.enums.LiveStreamPlatform
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.utils.UDCommonUtils
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LiveStreamProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var liveStreamRepository: LiveStreamRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var agoraClient: AgoraClient

    @Autowired
    private lateinit var agoraProperties: AgoraProperties
    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    fun getLiveStream(streamId: String): LiveStream? =
        try {
            val streams = liveStreamRepository.findAllByStreamPlatformAndStreamId(streamPlatform = LiveStreamPlatform.UNBOX, streamId = streamId)
            if (streams.size > 1) {
                error("More than one live stream has same streamId: $streamId")
            }
            streams.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Live Stream for $streamId failed.")
            e.printStackTrace()
            null
        }

    fun save(request: SaveLiveStreamRequest) : LiveStream? {
        try {
            val loggedInUserId = securityProvider.getFirebaseAuthUser()!!.getUserIdToUse()
            val isAdmin = UDCommonUtils.isAdmin(loggedInUserId)

            if (!isAdmin) {
                error("User is not admin. Only admins can save live streams.")
            }

            val stream = LiveStream(
                streamPlatform = request.streamPlatform,
                streamId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.LIV.name),
                streamerUserId = request.streamerUserId,
                startAt = DateUtils.getInstantFromLocalDateTime(DateUtils.parseEpochInSeconds(request.startAt)),
                endAt = DateUtils.getInstantFromLocalDateTime(DateUtils.parseEpochInSeconds(request.endAt)),
                headerImage = request.headerImage.convertToString(),
                title = request.title,
                subTitle = request.subTitle,
                createdAt = DateUtils.getInstantNow(),
            )
            return liveStreamRepository.save(stream)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error in saving live stream: ${e.message} ${request.toString()}")
            return null
        }
    }

    fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsRequestResponse {
        val result = getFeedForLiveStreams(request)

        // TODO: Optimize this so that we have a table on daily basis for all active streams
        // instead of querying all streams everytime
        val activeStreams = (result.content ?: emptyList()).filterNotNull().filter {
            logger.info("LiveStream: ${it.toString()}")
            logger.info("now: ${DateUtils.getInstantNow()}")
            logger.info("it.endAt: ${it.endAt}")
            DateUtils.getInstantNow().isBefore(it.endAt)
        }

        return AllActiveLiveStreamsRequestResponse(
            streams = activeStreams.map { it.toSavedLiveStreamResponse() },
            count = activeStreams.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun getFeedForLiveStreams(request: GetAllActiveLiveStreamsRequest): CassandraPageV2<LiveStream> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val streams = liveStreamRepository.findAllByStreamPlatform(request.liveStreamPlatform, pageRequest as Pageable)
        return CassandraPageV2(streams)
    }

    fun get(streamId: String): LiveStream? {
        return getLiveStream(streamId)
    }

    fun streamJoinedOrLeft(request: LiveStreamJoinedOrLeftRequest) {
        val stream = getLiveStream(request.streamId)!!
        val loggedInUserId = securityProvider.getFirebaseAuthUser()!!.getUserIdToUse()
        val user = userV2Provider.getUser(loggedInUserId)!!

        var joinedOrLeftText = "joined"

        if (request.liveStreamJoinStatus == LiveStreamJoinStatus.LEFT) {
            joinedOrLeftText = "left"
        }

        val message = LiveStreamChatMessage(
            liveStreamData = stream.toSavedLiveStreamResponse(),
            createdAt = DateUtils.getEpochNow(),
            senderUserId = loggedInUserId,
            text = "${user.fullName ?: user.handle} $joinedOrLeftText the live stream",
        )

        FirestoreClient.getFirestore()
            .collection("live_stream_chats")
            .document(stream.streamId)
            .collection("chat_messages")
            .add(message)


        // channel name is same as streamId
        val response: HttpResponse<JsonNode> = Unirest
            .get("https://api.agora.io/dev/v1/channel/user/${agoraProperties.appId}/${request.streamId}")
            .header("Authorization", agoraClient.getAuthorizationHeader())
            .header("Content-Type", "application/json")
            .asJson()

        logger.info("Response: ${response.body.toString()}")

        if (response.status == 200) {
            FirestoreClient.getFirestore()
                .collection("live_stream_metadata")
                .document(stream.streamId)
                .set(LiveStreamMetadataResponse(
                    totalAudience = getCount(request, response),
                ))
        } else {
            logger.error("Failed update live stream metadata response: ${response.toString()}")
        }

    }

    fun getCount(request: LiveStreamJoinedOrLeftRequest, response: HttpResponse<JsonNode>): Long {
        val count = try {
            response.body.`object`.getJSONObject("data").getLong("audience_total")
        } catch (e: Exception) {
            val isSuccessful = response.body.`object`.getBoolean("success")
            if (isSuccessful) {
                0L
            } else {
                logger.error("Failed to get live stream count: request: ${request.toString()} response: ${response.body.toString()}")
                throw e
            }
        }
        return count
    }

}
