package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import com.server.common.utils.DateUtils
import com.server.dk.entities.Company
import com.server.dk.enums.DiscountType
import com.server.shop.enums.ExistenceType
import com.server.shop.enums.DiscountTypeV3
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "discount_v3")
class DiscountV3 : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""


    var title: String = ""
    var description: String = ""
    var mediaDetails: String = "" // MediaDetailsV3 -> String

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    val existenceType: ExistenceType = ExistenceType.CAN_EXIST_WITH_SAME_TYPE

    @Enumerated(EnumType.STRING)
    val discountType: DiscountTypeV3 = DiscountTypeV3.FLAT_IN_PERCENT
    val discountAmount: Double = 0.0 // 100 rupees flat discount or 10% Discount with min order and max amount defined

    // Constraints
    // Max and Min
    var minOrderInPaisa: Long = 0
    var maxDiscountInPaisa: Long = 0

    // Customers
    var sameCustomerCount: Int = 1 // 1: Same customer can use it only one, similarly any other value
    var visibleToCustomer: Boolean = true

    // Time
    val startTime: LocalDateTime = DateUtils.dateTimeNow()
    val endTime: LocalDateTime = DateUtils.dateTimeNow().plusWeeks(1) // Default to a week

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}
