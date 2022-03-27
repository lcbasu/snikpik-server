package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "cart_item_v3")
class CartItemV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""


    var totalUnits: Long = 0

    // These 4 fields can change in future so save the values when the order is placed.
    // Start with null. And when the value is null we use the value of the product variant
    var taxPerUnitInPaisaPaid: Long? = 0
    var pricePerUnitInPaisaPaid: Long? = 0
    var totalTaxInPaisaPaid: Long? = 0
    var totalMrpInPaisa: Long? = 0
    var totalSellingPriceInPaisa: Long? = 0
    var totalPriceWithoutTaxInPaisaPaid: Long? = 0

    // Adding these values at cart level as well as may be some cart item might have delivery date that is different from the order date.
    // Max date to which we can deliver the order
    var maxDeliveryDateTime: LocalDateTime? = null

    // Date on which we promised that we will deliver the order, always less than or equal to maxDeliveryDateTime
    var promisedDeliveryDateTime: LocalDateTime? = null

    // Actual delivery date of the order
    var deliveredOnDateTime: LocalDateTime? = null

    // If added to cart from a post
    var postId: String? = null

    // This cart item will ALWAYS belong to a order
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrderV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariantV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    var brand: Brand? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

}
