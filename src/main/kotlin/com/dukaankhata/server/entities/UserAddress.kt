package com.dukaankhata.server.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class UserAddress : Auditable() {
    @EmbeddedId
    var id: UserAddressKey? = null

    var name: String? = null
    var firstOrderFromThisAddressAt: LocalDateTime? = null
    var lastOrderFromThisAddressAt: LocalDateTime? = null
    var totalOrdersFromThisAddressCount: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    var user: User? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("address_id")
    @JoinColumn(name = "address_id")
    var address: Address? = null;
}
