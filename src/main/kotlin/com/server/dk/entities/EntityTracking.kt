package com.server.dk.entities

import com.server.dk.enums.EntityType
import com.server.common.enums.TrackingType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.TrackingData
import javax.persistence.*

@Entity
class EntityTracking : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var entityType: EntityType = EntityType.COMPANY

    @Enumerated(EnumType.STRING)
    var trackingType: TrackingType = TrackingType.VIEW

    var trackingData: String = "" // TrackingData object ->

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariant? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    var collection: Collection? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

fun EntityTracking.getTrackingData(): TrackingData? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(trackingData, TrackingData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

