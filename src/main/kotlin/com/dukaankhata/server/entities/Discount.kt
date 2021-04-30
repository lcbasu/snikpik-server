package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.DiscountType
import com.dukaankhata.server.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

// This is IMMUTABLE once created
// we can only change endAt even if we want to cancel/delete this discount
// this discount
// DO NOT Allow any other edits.
// Id endAt > current time -> only then allow update
// else no update possible
@Entity
class Discount : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var promoCode: String = ""

    @Enumerated(EnumType.STRING)
    var discountType: DiscountType = DiscountType.ABSOLUTE
    var discountAmount: Long = 0 // 100 rupees flat discount or 10% Discount with min order and max amount defined

    // Constraints
    // Max and Min
    var minOrderValueInPaisa: Long = 0
    var maxDiscountAmountInPaisa: Long = 0

    // Customers
    var sameCustomerCount: Int = 1 // 1: Same customer can use it only one, similarly any other value
    var visibleToCustomer: Boolean = true

    // Time
    var startAt: LocalDateTime = DateUtils.dateTimeNow()
    var endAt: LocalDateTime = DateUtils.dateTimeNow().plusWeeks(1) // Default to a week

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
