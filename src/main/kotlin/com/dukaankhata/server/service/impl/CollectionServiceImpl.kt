package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse
import com.dukaankhata.server.dto.toSavedCollectionResponse
import com.dukaankhata.server.service.CollectionService
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CollectionServiceImpl : CollectionService() {
    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var collectionUtils: CollectionUtils

    override fun saveCollection(saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse? {
        val requestContext = authUtils.validateRequest(
            companyId = saveCollectionRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedCollection = collectionUtils.saveCollection(company, requestContext.user, saveCollectionRequest) ?: error("Error while saving collection")
        return savedCollection.toSavedCollectionResponse()
    }
}
