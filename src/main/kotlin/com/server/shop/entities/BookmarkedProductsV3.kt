package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "bookmarked_products_v3")
class BookmarkedProductsV3 : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    var bookmarked: Boolean = false

    // If added to cart from a post
    var postId: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariantV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}
