package com.server.ud.service.view

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.ResourceViewRequest
import com.server.ud.dto.ResourceViewsReportDetail
import com.server.ud.dto.ResourceViewsReportDetailForUser
import com.server.ud.dto.toResourceViewId
import com.server.ud.provider.deferred.DeferredProcessingProvider
import com.server.ud.provider.view.ResourceViewByUserProvider
import com.server.ud.provider.view.ResourceViewsCountByResourceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ResourceViewServiceImpl : ResourceViewService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var resourceViewsCountByResourceProvider: ResourceViewsCountByResourceProvider

    @Autowired
    private lateinit var resourceViewByUserProvider: ResourceViewByUserProvider

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    override fun saveResourceView(request: ResourceViewRequest): Boolean? {
        deferredProcessingProvider.deferProcessingForView(request.toResourceViewId())
        return true
    }

    // Get this details for both logged in user as well as guest user
    override fun getResourceViewsDetail(resourceId: String): ResourceViewsReportDetail {
        val userDetailsFromToken = securityProvider.getFirebaseAuthUser()
        val viewsCountByResource = resourceViewsCountByResourceProvider.getResourceViewsCountByResource(resourceId)?.viewsCount ?: 0
        val viewed = userDetailsFromToken?.let {
            resourceViewByUserProvider.getLastView(
                ResourceViewRequest(
                    userId = userDetailsFromToken.getUserIdToUse(),
                    resourceId = resourceId,
                )
            ) != null
        } ?: false
        return ResourceViewsReportDetail(
            resourceId = resourceId,
            views = viewsCountByResource,
            userLevelInfo = userDetailsFromToken?.let {
                ResourceViewsReportDetailForUser(
                    userId = userDetailsFromToken.getUserIdToUse(),
                    viewed = viewed
                )
            }
        )
    }
}
