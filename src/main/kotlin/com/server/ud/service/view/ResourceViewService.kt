package com.server.ud.service.view

import com.server.ud.dto.ResourceViewsReportDetail
import com.server.ud.dto.ResourceViewRequest
import com.server.ud.dto.ResourceViewsCountResponse
import com.server.ud.enums.ResourceType

abstract class ResourceViewService {
    abstract fun saveResourceView(request: ResourceViewRequest): Boolean?
    abstract fun getResourceViewsDetail(resourceId: String): ResourceViewsReportDetail
    abstract fun getResourceViewsCount(resourceId: String): ResourceViewsCountResponse?
}
