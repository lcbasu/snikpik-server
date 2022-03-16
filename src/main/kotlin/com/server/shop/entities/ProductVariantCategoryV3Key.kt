package com.server.shop.entities

import com.server.shop.enums.ProductCategoryV3
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ProductVariantCategoryV3Key: Serializable {

    @Column(name = "product_variant_id")
    var productVariantId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    var category: ProductCategoryV3 = ProductCategoryV3.DECOR
}
