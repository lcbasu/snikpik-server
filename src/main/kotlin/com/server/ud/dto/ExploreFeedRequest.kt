package com.server.ud.dto

import com.server.ud.enums.CategoryV2
import javax.validation.constraints.Max
import javax.validation.constraints.Min

class ExploreFeedRequest (
    val category: CategoryV2,
    val forDate: String, // YYYY-MM-DD
    @Min(1)
    @Max(1000)
    val limit: Int = 10,
    val pagingState: String? = null,
)
