package com.server.dk.entities

import com.server.dk.enums.CategoryGroup
import com.server.dk.enums.DKShopStatus
import com.server.dk.enums.SalaryPaymentSchedule
import com.server.dk.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class Company : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""
    var name: String = ""
    var location: String = ""
    var workingMinutes: Int = 0
    var totalDueAmountInPaisa: Long = 0
//    var userId: String = ""
    @Enumerated(EnumType.STRING)
    var salaryPaymentSchedule: SalaryPaymentSchedule = SalaryPaymentSchedule.LAST_DAY_OF_MONTH

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null;

    var logo: String? = "" // MediaDetails object as string
    var absoluteMobile: String? = ""
    var countryCode: String? = ""

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("company_id")
    @JoinColumn(name = "default_shop_address_id")
    var defaultShopAddress: Address? = null;

    // DK Store related fields
    @Enumerated(EnumType.STRING)
    var dkShopStatus: DKShopStatus? = DKShopStatus.ONLINE

    @Column(unique = true)
    // Update it with the last updated username from
    // company_username table
    var username: String? = ""

    var totalOrderAmountInPaisa: Long? = 0
    var totalStoreViewCount: Long? = 0
    var totalStoreClickCount: Long? = 0
    var totalOrdersCount: Long? = 0
    var totalUnitsOrdersCount: Long? = 0
    var totalProductsViewCount: Long? = 0
    var totalProductsClickCount: Long? = 0

    var defaultAddressId: String? = "" // Address table Id

    @Enumerated(EnumType.STRING)
    var categoryGroup: CategoryGroup? = CategoryGroup.General
}

fun Company.getLogoDetails(): MediaDetails? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(logo, MediaDetails::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

