package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CompanyAddressKey: Serializable {

    @Column(name = "company_id")
    var companyId: String = ""

    @Column(name = "address_id")
    var addressId: String = ""
}
