package com.server.ud.provider.live_stream

import com.google.cloud.firestore.FieldValue
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
import com.server.ud.dao.live_stream.LiveStreamSubscribedByUserRepository
import com.server.ud.dao.live_stream.SubscribedLiveStreamUsersByStreamRepository
import com.server.ud.dao.live_stream.SubscribedLiveStreamsByUserRepository
import com.server.ud.dto.*
import com.server.ud.entities.live_stream.LiveStream
import com.server.ud.entities.live_stream.LiveStreamSubscribedByUser
import com.server.ud.entities.live_stream.SubscribedLiveStreamUsersByStream
import com.server.ud.entities.live_stream.SubscribedLiveStreamsByUser
import com.server.ud.entities.post.PostsByFollowing
import com.server.ud.entities.post.PostsByFollowingTracker
import com.server.ud.entities.post.toPostsByFollowing
import com.server.ud.enums.LiveStreamJoinStatus
import com.server.ud.enums.LiveStreamPlatform
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.utils.UDCommonUtils
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var liveStreamSubscribedByUserRepository: LiveStreamSubscribedByUserRepository

    @Autowired
    private lateinit var subscribedLiveStreamsByUserRepository: SubscribedLiveStreamsByUserRepository

    @Autowired
    private lateinit var subscribedLiveStreamUsersByStreamRepository: SubscribedLiveStreamUsersByStreamRepository

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
            val result = liveStreamRepository.save(stream)
            setLiveStreamLikeCount(result.streamId)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error in saving live stream: ${e.message} ${request.toString()}")
            return null
        }
    }

    fun getAllActiveLiveStreams(request: GetAllActiveLiveStreamsRequest): AllActiveLiveStreamsResponse {
        val result = getFeedForLiveStreams(request.liveStreamPlatform, request.limit, request.pagingState)

        // TODO: Optimize this so that we have a table on daily basis for all active streams
        // instead of querying all streams everytime
        val activeStreams = (result.content ?: emptyList()).filterNotNull().filter {
            logger.info("LiveStream: ${it.toString()}")
            logger.info("now: ${DateUtils.getInstantNow()}")
            logger.info("it.endAt: ${it.endAt}")
            DateUtils.getInstantNow().isBefore(it.endAt)
        }

        return AllActiveLiveStreamsResponse(
            streams = activeStreams.map { it.toSavedLiveStreamResponse() },
            count = activeStreams.size,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getAllLiveStreams(request: GetAllLiveStreamsRequest): AllLiveStreamsResponse {
        val result = getFeedForLiveStreams(request.liveStreamPlatform, request.limit, request.pagingState)

        return AllLiveStreamsResponse(
            streams = (result.content ?: emptyList()).filterNotNull().map { it.toSavedLiveStreamResponse() },
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun getFeedForLiveStreams(liveStreamPlatform: LiveStreamPlatform, limit: Int, pagingState: String?): CassandraPageV2<LiveStream> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val streams = liveStreamRepository.findAllByStreamPlatform(liveStreamPlatform, pageRequest as Pageable)
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

    fun like(request: LiveStreamLikedRequest) {
        increaseLiveStreamLikeCount(request.streamId)
    }

    private fun increaseLiveStreamLikeCount (streamId: String) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("likes_count_by_stream")
                .document(streamId)
                .update("count", FieldValue.increment(1))
        }
    }

    private fun setLiveStreamLikeCount (streamId: String) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("likes_count_by_stream")
                .document(streamId)
                .set(LikesCountForFirebase(0))
        }
    }

    fun subscribe(request: LiveStreamSubscribeRequest): LiveStreamSubscribedResponse {
        val stream = getLiveStream(request.streamId) ?: error("Stream not found for id: ${request.streamId}")
        val loggedInUserId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse() ?: error("User not logged in")
        val subscribedObject = liveStreamSubscribedByUserRepository.save(LiveStreamSubscribedByUser(
            streamId = request.streamId,
            subscriberUserId = loggedInUserId,
            subscribed = true,
        ))

        subscribedLiveStreamsByUserRepository.save(
            SubscribedLiveStreamsByUser(
                subscriberUserId = loggedInUserId,
                startAt = stream.startAt,
                streamId = stream.streamId,
                streamPlatform = stream.streamPlatform,
                streamerUserId = stream.streamerUserId,
                endAt = stream.endAt,
                headerImage = stream.headerImage,
                title = stream.title,
                subTitle = stream.subTitle,
                createdAt = stream.createdAt,
                subscribedAt = subscribedObject.createdAt
            ))

        subscribedLiveStreamUsersByStreamRepository.save(
            SubscribedLiveStreamUsersByStream(
                subscriberUserId = loggedInUserId,
                startAt = stream.startAt,
                streamId = stream.streamId,
                streamPlatform = stream.streamPlatform,
                streamerUserId = stream.streamerUserId,
                endAt = stream.endAt,
                headerImage = stream.headerImage,
                title = stream.title,
                subTitle = stream.subTitle,
                createdAt = stream.createdAt,
                subscribedAt = subscribedObject.createdAt
            )
        )

        return LiveStreamSubscribedResponse(
            stream = stream.toSavedLiveStreamResponse(),
            subscribed = true,
        )
    }

    fun getAllSubscribedLiveStreams(request: GetAllSubscribedStreamsRequest): AllSubscribedLiveStreamsResponse? {
        val result = _getAllSubscribedLiveStreams(request)
        return AllSubscribedLiveStreamsResponse(
            subscribedStreams = result.content?.mapNotNull { it?.toSubscribedLiveStreamResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    private fun _getAllSubscribedLiveStreams(request: GetAllSubscribedStreamsRequest): CassandraPageV2<SubscribedLiveStreamsByUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val loggedInUserId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse() ?: error("User not logged in")
        val streams = subscribedLiveStreamsByUserRepository.findAllBySubscriberUserId(loggedInUserId, pageRequest as Pageable)
        return CassandraPageV2(streams)
    }

    fun getAllSubscribedLiveStreamUsersByStream(streamId: String): List<SubscribedLiveStreamUsersByStream> {
        val limit = 10
        var pagingState = ""

        val trackedSubscribedLiveStreamUsersByStream = mutableListOf<SubscribedLiveStreamUsersByStream>()

        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val results = subscribedLiveStreamUsersByStreamRepository.findAllByStreamId(
            streamId,
            pageRequest as Pageable
        )
        val slicedResult = CassandraPageV2(results)
        trackedSubscribedLiveStreamUsersByStream.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        while (hasNext) {
            pagingState = slicedResult.pagingState ?: ""
            val nextPageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
            val nextUsers = subscribedLiveStreamUsersByStreamRepository.findAllByStreamId(
                streamId,
                nextPageRequest as Pageable
            )
            val nextSlicedResult = CassandraPageV2(nextUsers)
            hasNext = nextSlicedResult.hasNext == true
            trackedSubscribedLiveStreamUsersByStream.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return trackedSubscribedLiveStreamUsersByStream
    }

    private fun _getLiveStreamSubscribedUsers(request: GetAllSubscribedUsersOfStreamRequest): CassandraPageV2<SubscribedLiveStreamUsersByStream> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val loggedInUserId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse() ?: error("User not logged in")
        val streams = subscribedLiveStreamUsersByStreamRepository.findAllByStreamId(request.streamId, pageRequest as Pageable)
        return CassandraPageV2(streams)
    }

    fun checkSubscribed(streamId: String): LiveStreamSubscribedResponse? {
        val stream = getLiveStream(streamId) ?: error("Stream not found for id: $streamId")
        val loggedInUserId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse() ?: error("User not logged in")

        val subscribedResult = liveStreamSubscribedByUserRepository.findAllByStreamIdAndSubscriberUserId(
            streamId,
            loggedInUserId
        )
        return LiveStreamSubscribedResponse(
            stream = stream.toSavedLiveStreamResponse(),
            subscribed = subscribedResult.size == 1 && subscribedResult.first().subscribed,

        )
    }


}
