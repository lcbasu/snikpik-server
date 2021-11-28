package com.server.ud.service.social

import com.server.ud.dto.SaveHashTagsRequest
import com.server.ud.dto.SavedHashTagsResponse

abstract class HashTagService {
    abstract fun saveHashTags(request: SaveHashTagsRequest): SavedHashTagsResponse?
}
