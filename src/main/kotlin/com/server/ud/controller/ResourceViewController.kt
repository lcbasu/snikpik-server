package com.server.ud.controller

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.ResourceViewRequest
import com.server.ud.dto.ResourceViewsReportDetail
import com.server.ud.dto.SaveResourceViewRequest
import com.server.ud.enums.ResourceType
import com.server.ud.service.view.ResourceViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/resourceView")
class ResourceViewController {

    @Autowired
    private lateinit var resourceViewService: ResourceViewService

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveResourceView(@RequestBody request: SaveResourceViewRequest): Boolean? {
        val user = securityProvider.getFirebaseAuthUser() ?: error("User must be logged in as guest or using some providers.")
        return resourceViewService.saveResourceView(
            ResourceViewRequest(
                userId = user.getUserIdToUse(),
                resourceId = request.resourceId
            ))
    }

    @RequestMapping(value = ["/getResourceViewsDetail"], method = [RequestMethod.GET])
    fun getResourceViewsDetail(@RequestParam resourceId: String): ResourceViewsReportDetail {
        return resourceViewService.getResourceViewsDetail(resourceId)
    }
}
