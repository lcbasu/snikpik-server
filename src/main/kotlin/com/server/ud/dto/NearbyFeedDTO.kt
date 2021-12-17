package com.server.ud.dto

data class NearbyFeedRequest (
    val zipcode: String,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

