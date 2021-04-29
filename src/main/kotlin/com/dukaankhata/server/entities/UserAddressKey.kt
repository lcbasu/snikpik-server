package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserAddressKey: Serializable {

    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "address_id")
    var addressId: String = ""
}
