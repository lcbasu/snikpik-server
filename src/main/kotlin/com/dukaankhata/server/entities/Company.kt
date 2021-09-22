package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.CategoryGroup
import com.dukaankhata.server.enums.DKShopStatus
import com.dukaankhata.server.enums.SalaryPaymentSchedule
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
    var totalOrdersCount: Long? = 0
    var totalProductsViewCount: Long? = 0

    var defaultAddressId: String? = "" // Address table Id

    @Enumerated(EnumType.STRING)
    var categoryGroup: CategoryGroup? = CategoryGroup.General
}
