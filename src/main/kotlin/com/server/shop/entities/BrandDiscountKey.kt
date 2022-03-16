package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class BrandDiscountKey: Serializable {

    @Column(name = "brand_id")
    var brandId: String = ""

    @Column(name = "discount_id")
    var discountId: String = ""
}
