package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "company_v3")
class CompanyV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var logo: String? = null // MediaDetailsV3 object -> Multiple Images or videos
    var headerBanner: String? = null // MediaDetailsV3 object -> Multiple Images or videos
    var marketingName: String = ""
    var legalName: String = ""
    var dateOfEstablishment: LocalDateTime = DateUtils.dateTimeNow()

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "head_office_address_id")
    var headOfficeAddress: AddressV3? = null;

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_address_id")
    var communicationAddress: AddressV3? = null;

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    var billingAddress: AddressV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

    var totalStoreViewCount: Long? = 0
    var totalStoreClickCount: Long? = 0

    var totalBrandsViewCount: Long? = 0
    var totalBrandsClickCount: Long? = 0

    var totalProductsViewCount: Long? = 0
    var totalProductsClickCount: Long? = 0

    var totalOrderAmountInPaisa: Long? = 0
    var totalOrdersCount: Long? = 0
    var totalUnitsOrdersCount: Long? = 0

    @OneToMany
    @JoinColumn(name = "company_id")
    var brands: Set<Brand> = emptySet()

    @OneToMany
    @JoinColumn(name = "company_id")
    var products: Set<ProductV3> = emptySet()

    @OneToMany
    @JoinColumn(name = "company_id")
    var productVariants: Set<ProductVariantV3> = emptySet()


}

fun CompanyV3.getLogoMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(logo)
    }
}

fun CompanyV3.getHeaderBannerMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(headerBanner)
    }
}
