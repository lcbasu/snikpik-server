package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Brand : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    @Column(unique = true)
    var handle: String = ""

    var logo: String = "" // MediaDetailsV3 object -> Multiple Images or videos
    var headerBanner: String = "" // MediaDetailsV3 object -> Multiple Images or videos
    var marketingName: String = ""
    var legalName: String = ""
    var dateOfEstablishment: LocalDateTime = DateUtils.dateTimeNow()

    var totalBrandsViewCount: Long? = 0
    var totalBrandsClickCount: Long? = 0

    var totalProductsViewCount: Long? = 0
    var totalProductsClickCount: Long? = 0

    var totalOrderAmountInPaisa: Long? = 0
    var totalOrdersCount: Long? = 0
    var totalUnitsOrdersCount: Long? = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    // This CascadeType array is to fix Multiple representations of the same entity
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

    @OneToMany
    @JoinColumn(name = "brand_id")
    var products: Set<ProductV3> = emptySet()

    @OneToMany
    @JoinColumn(name = "brand_id")
    var productVariants: Set<ProductVariantV3> = emptySet()
}

fun Brand.getLogoMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(logo)
    }
}

fun Brand.getHeaderBannerMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(headerBanner)
    }
}
