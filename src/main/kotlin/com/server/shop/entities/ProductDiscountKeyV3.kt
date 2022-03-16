package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ProductDiscountKeyV3: Serializable {

    @Column(name = "product_id")
    var productId: String = ""

    @Column(name = "discount_id")
    var discountId: String = ""
}
