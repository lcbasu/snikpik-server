package com.dukaankhata.server.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class CompanyAddress : Auditable() {
    @EmbeddedId
    var id: CompanyAddressKey? = null

    var name: String? = null
    var firstOrderFromThisAddressAt: LocalDateTime? = null
    var lastOrderFromThisAddressAt: LocalDateTime? = null
    var totalOrdersFromThisAddressCount: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("address_id")
    @JoinColumn(name = "address_id")
    var address: Address? = null;
}
