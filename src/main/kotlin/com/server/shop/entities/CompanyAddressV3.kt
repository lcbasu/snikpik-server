package com.server.shop.entities

import com.server.common.entities.Auditable
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "company_address_v3")
class CompanyAddressV3 : Auditable() {
    @EmbeddedId
    var id: CompanyAddressKeyV3? = null

    var name: String? = null

    var firstOrderFromThisAddressAt: LocalDateTime? = null
    var lastOrderFromThisAddressAt: LocalDateTime? = null
    var totalOrdersFromThisAddressCount: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("address_id")
    @JoinColumn(name = "address_id")
    var address: AddressV3? = null;
}
