package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.CategoryGroupResponse
import com.dukaankhata.server.dto.CategoryGroupsResponse
import com.dukaankhata.server.enums.CategoryGroup
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.service.CatalogueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CatalogueServiceImpl : CatalogueService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    override fun getCategoryGroups(): CategoryGroupsResponse {
        authProvider.validateRequest()
        return CategoryGroupsResponse(
            CategoryGroup.values().map {
                CategoryGroupResponse(
                    id = it.id,
                    displayName = it.displayName,
                    description = it.description,
                    mediaDetails = it.mediaDetails
                )
            }
        )
    }
}
