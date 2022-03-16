package com.server.shop.entities

import com.server.shop.enums.ProductVertical
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ProductVariantVerticalKey: Serializable {

    @Column(name = "product_variant_id")
    var productVariantId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "vertical")
    var vertical: ProductVertical = ProductVertical.SOFA
}
