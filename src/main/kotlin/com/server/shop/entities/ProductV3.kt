package com.server.shop.entities

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.shop.enums.ProductCategoryV3
import com.server.shop.enums.ProductStatusV3
import com.server.shop.enums.ProductUnitV3
import javax.persistence.*

@Entity(name = "product_v3")
class ProductV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""
//
//    var title: String = ""
//
//    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos

    // Change based on the overall status of all the variants
    @Enumerated(EnumType.STRING)
    var status: ProductStatusV3 = ProductStatusV3.DRAFT

    var categories: String = "" // AllProductCategories as String

    // Unit
    @Enumerated(EnumType.STRING)
    @Column(updatable = false) // You can not update the product unit after this is published
    var productUnit: ProductUnitV3 = ProductUnitV3.PIECE

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "default_variant_id")
    var defaultVariant: ProductVariantV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    var brand: Brand? = null;

    // Keeping company:Company & addedBy:User  reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    // This CascadeType array is to fix Multiple representations of the same entity
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

    @OneToMany
    @JoinColumn(name = "product_id")
    var productVariants: Set<ProductVariantV3> = emptySet()
}

data class AllProductCategories (
    val categories: Set<ProductCategoryV3>
)

fun ProductV3.getAllProductCategories(): AllProductCategories {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllProductCategories::class.java)
        } catch (e: Exception) {
            AllProductCategories(emptySet())
        }
    }
}
