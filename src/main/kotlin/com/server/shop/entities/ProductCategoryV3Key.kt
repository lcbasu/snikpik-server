package com.server.shop.entities

import com.server.shop.enums.ProductCategoryV3
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ProductCategoryV3Key: Serializable {

    @Column(name = "product_id")
    var productId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    var category: ProductCategoryV3 = ProductCategoryV3.DECOR
}
