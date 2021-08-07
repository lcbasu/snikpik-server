package com.dukaankhata.server.entities

import com.dukaankhata.server.dto.VariantInfo
import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.persistence.*

@Entity
class ProductVariant : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var title: String = ""

    var taxPerUnitInPaisa: Long = 0 // If zero then tax is included otherwise excluded

    var originalPricePerUnitInPaisa: Long = 0
    var sellingPricePerUnitInPaisa: Long = 0

    var totalUnitInStock: Long = 0

    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos
    var colorInfo: String = "" // VariantInfo object
    var sizeInfo: String = "" // VariantInfo object

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null;

    // Keeping company:Company & addedBy:User  reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

fun ProductVariant.getMediaDetails(): MediaDetails {
    this.apply {
        return jacksonObjectMapper().readValue(mediaDetails, MediaDetails::class.java)
    }
}

fun ProductVariant.getColorInfo(): VariantInfo? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(colorInfo, VariantInfo::class.java)
        } catch (e: Exception) {
            null
        }

    }
}

fun ProductVariant.getSizeInfo(): VariantInfo? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(sizeInfo, VariantInfo::class.java)
        } catch (e: Exception) {
            null
        }

    }
}
