package com.dukaankhata.server.model

data class MediaDetail(
    val mediaUrl: String,
    val mimeType: String
)

data class MediaDetails(
    val media: List<MediaDetail>
)
