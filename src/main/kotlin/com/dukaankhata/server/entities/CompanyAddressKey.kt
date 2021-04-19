package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CompanyAddressKey: Serializable {

    @Column(name = "company_id")
    var companyId: Long = -1

    @Column(name = "address_id")
    var addressId: Long = -1
}
