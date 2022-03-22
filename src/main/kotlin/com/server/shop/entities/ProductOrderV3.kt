package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import com.server.dk.enums.DeliveryTimeId
import com.server.shop.enums.DeliveryTimeIdV3
import com.server.shop.enums.OrderPaymentModeV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.enums.ProductOrderType
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "product_order_v3")
class ProductOrderV3 : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    var type: ProductOrderType = ProductOrderType.REGULAR_ORDER

    var totalPricePayableInPaisa: Long = 0 // (priceOfProductsWithoutTaxInPaisa + totalTaxInPaisa + deliveryChargeInPaisa) - discountInPaisa
    var totalDiscountInPaisa: Long = 0 // Sum of all the applied promotions
    var totalTaxInPaisa: Long = 0

    var totalCartItems: Long = 0
    var totalUnitsInAllCarts: Long = 0

    var deliveryChargeInPaisa: Long = 0

    var priceOfCartItemsWithoutTaxInPaisa: Long = 0

    var orderedOnDateTime: LocalDateTime? = null

    // Min date of delivery (maxDeliveryDateTime) for any CartItemV3
    var minOfMaxDeliveryDateTime: LocalDateTime? = null
    // Max date of delivery (maxDeliveryDateTime) for any CartItemV3
    var maxOfMaxDeliveryDateTime: LocalDateTime? = null

    // Min date of delivery (promisedDeliveryDateTime) for any CartItemV3
    var minOfPromisedDeliveryDateTime: LocalDateTime? = null
    // Max date of delivery (promisedDeliveryDateTime) for any CartItemV3
    var maxOfPromisedDeliveryDateTime: LocalDateTime? = null

    // In normal cases, these both would be same except for cases when each item is delivered on separate delivery time
    var firstCartItemDeliveredOnDateTime: LocalDateTime? = null
    var lastCartItemDeliveredOnDateTime: LocalDateTime? = null

    var replaceableTillDateTime: LocalDateTime? = null
    var refundableTillDateTime: LocalDateTime? = null

    var razorpayOrderId: String? = null

    var razorpayPaymentId: String? = null

    @Enumerated(EnumType.STRING)
    var orderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.DRAFT

    @Enumerated(EnumType.STRING)
    var paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.NONE

    // The ProductOrderPaymentV3 ID which was successful for this product Order
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "success_payment_id")
    var successPayment: ProductOrderPaymentV3? = null;

    @Enumerated(EnumType.STRING)
    var deliveryTimeId: DeliveryTimeIdV3 = DeliveryTimeIdV3.DAYS_1_3

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    var deliveryAddress: AddressV3? = null;

    // Coupons are applied on the order level
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    var appliedCoupon: CouponV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;

    // Sets of referenced objets
    @OneToMany
    @JoinColumn(name = "product_order_id")
    var cartItems: Set<CartItemV3> = emptySet()
}
