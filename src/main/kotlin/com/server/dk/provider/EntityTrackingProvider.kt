package com.server.dk.provider

import EntityInteractionRequest
import com.server.dk.dao.EntityTrackingRepository
import com.server.dk.dto.*
import com.server.dk.entities.*
import com.server.dk.enums.EntityType
import com.server.dk.enums.ProductStatus
import com.server.dk.enums.ReadableIdPrefix
import com.server.dk.enums.TrackingType
import com.server.dk.model.convertToString
import convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EntityTrackingProvider {

    @Autowired
    private lateinit var entityTrackingRepository: EntityTrackingRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    fun saveProduct(request: EntityInteractionRequest) : EntityTracking? {
        return try {
            val newEntityTracking = EntityTracking()
            newEntityTracking.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.ETR.name)
            newEntityTracking.addedBy = authProvider.getRequestUserEntity()
            newEntityTracking.company = request.companyServerIdOrUsername?.let { companyProvider.getCompanyByServerIdOrUsername(it) }
            newEntityTracking.product = request.productId?.let { productProvider.getProduct(it) }
            newEntityTracking.productVariant = request.productVariantId?.let { productVariantProvider.getProductVariant(it) }
            newEntityTracking.collection = request.collectionId?.let { collectionProvider.getCollection(it) }
            newEntityTracking.trackingType = request.trackingType
            newEntityTracking.trackingData = request.trackingData?.convertToString() ?: ""
            newEntityTracking.entityType = request.entityType
            val savedEntityTracking = entityTrackingRepository.save(newEntityTracking)
            updateDependentEntities(savedEntityTracking)
            savedEntityTracking
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun updateDependentEntities(
        savedEntityTracking: EntityTracking
    ) {
        when (savedEntityTracking.entityType) {
            EntityType.COMPANY -> {
                when (savedEntityTracking.trackingType) {
                    TrackingType.CLICK ->
                        companyProvider.increaseClick(savedEntityTracking)
                    TrackingType.VIEW ->
                        companyProvider.increaseView(savedEntityTracking)
                }
            }
            EntityType.PRODUCT -> {
                when (savedEntityTracking.trackingType) {
                    TrackingType.CLICK ->
                        productProvider.increaseClick(savedEntityTracking)
                    TrackingType.VIEW ->
                        productProvider.increaseView(savedEntityTracking)
                }
            }
            EntityType.PRODUCT_VARIANT -> {
                when (savedEntityTracking.trackingType) {
                    TrackingType.CLICK ->
                        productVariantProvider.increaseClick(savedEntityTracking)
                    TrackingType.VIEW ->
                        productVariantProvider.increaseView(savedEntityTracking)
                }
            }
            EntityType.COLLECTION -> {
                when (savedEntityTracking.trackingType) {
                    TrackingType.CLICK ->
                        collectionProvider.increaseClick(savedEntityTracking)
                    TrackingType.VIEW ->
                        collectionProvider.increaseView(savedEntityTracking)
                }
            }
        }
    }

}
