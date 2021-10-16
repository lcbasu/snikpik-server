package com.server.dk.service.impl

import com.server.dk.dto.*
import com.server.dk.provider.AuthProvider
import com.server.dk.provider.CollectionProvider
import com.server.dk.service.CollectionService
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
            companyServerIdOrUsername = saveCollectionRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val company = requestContext.company ?: error("Company should be present")
        val savedCollection = collectionProvider.saveCollection(company, requestContext.user, saveCollectionRequest) ?: error("Error while saving collection")
        return savedCollection.toSavedCollectionResponse()
    }

    override fun getAllCollection(companyServerId: String): AllCollectionsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyServerId
        )
        val company = requestContext.company ?: error("Company should be present")
        return collectionProvider.getAllCollection(company)
    }

    override fun getAllCollectionWithProducts(companyServerId: String): AllCollectionsWithProductsResponse {
        val requestContext = authProvider.validateRequest(
            companyServerIdOrUsername = companyServerId
        )
        val company = requestContext.company ?: error("Company should be present")
        return collectionProvider.getAllCollectionWithProducts(company)
    }
}
