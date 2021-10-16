package com.server.dk.service.impl

import com.server.dk.dto.CategoryGroupResponse
import com.server.dk.dto.CategoryGroupsResponse
import com.server.dk.enums.CategoryGroup
import com.server.dk.provider.AuthProvider
import com.server.dk.service.CatalogueService
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
