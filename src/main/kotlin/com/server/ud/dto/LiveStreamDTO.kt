package com.server.ud.dto

import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.entities.live_stream.LiveStream
import com.server.ud.enums.LiveStreamJoinStatus
import com.server.ud.enums.LiveStreamPlatform

data class SaveLiveStreamRequest (
    val streamPlatform: LiveStreamPlatform,
    val streamerUserId: String,
    val startAt: Long,
    val endAt: Long,
    val headerImage: MediaDetailsV2,
    val title: String,
    val subTitle: String,
)

data class SavedLiveStreamResponse (
    val streamPlatform: LiveStreamPlatform,
    val streamId: String,
    val streamerUserId: String,
    val startAt: Long,
    val endAt: Long,
    val headerImage: MediaDetailsV2,
    val title: String,
    val subTitle: String,
    val createdAt: Long,
)

data class LiveStreamLikedRequest (
    val streamId: String,
)

data class LiveStreamJoinedOrLeftRequest (
    val streamId: String,
    val liveStreamJoinStatus: LiveStreamJoinStatus,
)

data class LiveStreamChatMessage (
    val liveStreamData: SavedLiveStreamResponse,
    val createdAt: Long,
    val senderUserId: String,
    val text: String?,
    val media: MediaDetailsV2? = null,
)

data class LiveStreamMetadataResponse(
    val totalAudience: Long,
)

data class LikesCountForFirebase (
    val count: Long,
)

data class AllActiveLiveStreamsRequestResponse (
    val streams: List<SavedLiveStreamResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class GetAllActiveLiveStreamsRequest (
    val liveStreamPlatform: LiveStreamPlatform,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)


fun LiveStream.toSavedLiveStreamResponse(): SavedLiveStreamResponse {
    this.apply {
        return SavedLiveStreamResponse(
            streamPlatform = streamPlatform,
            streamId = streamId,
            streamerUserId = streamerUserId,
            startAt = DateUtils.getEpoch(startAt),
            endAt = DateUtils.getEpoch(endAt),
            headerImage = getMediaDetailsFromJsonString(headerImage),
            title = title,
            subTitle = subTitle,
            createdAt = DateUtils.getEpoch(createdAt),
        )
    }
}

