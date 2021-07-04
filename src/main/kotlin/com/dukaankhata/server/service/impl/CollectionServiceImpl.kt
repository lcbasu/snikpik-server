package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.dto.SavedCollectionResponse
import com.dukaankhata.server.dto.toSavedCollectionResponse
import com.dukaankhata.server.service.CollectionService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.CollectionProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CollectionServiceImpl : CollectionService() {
    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    override fun saveCollection(saveCollectionRequest: SaveCollectionRequest): SavedCollectionResponse? {
        val requestContext = authProvider.validateRequest(
            companyId = saveCollectionRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedCollection = collectionProvider.saveCollection(company, requestContext.user, saveCollectionRequest) ?: error("Error while saving collection")
        return savedCollection.toSavedCollectionResponse()
    }
}
