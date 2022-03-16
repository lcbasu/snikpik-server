package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CompanyAddressKeyV3: Serializable {

    @Column(name = "company_id")
    var companyId: String = ""

    @Column(name = "address_id")
    var addressId: String = ""
}
