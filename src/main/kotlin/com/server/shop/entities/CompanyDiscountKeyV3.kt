package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CompanyDiscountKeyV3: Serializable {

    @Column(name = "company_id")
    var companyId: String = ""

    @Column(name = "discount_id")
    var discountId: String = ""
}
