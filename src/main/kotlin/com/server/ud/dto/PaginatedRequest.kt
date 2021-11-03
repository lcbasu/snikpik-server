package com.server.ud.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min

class PaginatedRequest (
    @Min(1)
    @Max(1000)
    val limit: Int = 10,
    val pagingState: String? = null
)
