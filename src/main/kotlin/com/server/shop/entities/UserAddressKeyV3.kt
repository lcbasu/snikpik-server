package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserAddressKeyV3: Serializable {

    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "address_id")
    var addressId: String = ""
}
