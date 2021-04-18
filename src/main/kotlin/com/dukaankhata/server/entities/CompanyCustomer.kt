package com.dukaankhata.server.entities

import com.dukaankhata.server.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class CompanyCustomer : Auditable() {
    @EmbeddedId
    var id: CompanyCustomerKey? = null
    var joinedAt: LocalDateTime = DateUtils.dateTimeNow()
    var firstOrderAt: LocalDateTime? = null
    var lastOrderAt: LocalDateTime? = null
    var totalOrdersCount: Long = 0
    var totalAmountSpent: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    var user: User? = null;
}
