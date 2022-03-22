package com.server.shop.enums

enum class ProductOrderType {
    // When you first add to cart and then buy
    REGULAR_ORDER,

    // When you click on "Buy Now" for any product
    BUY_NOW_ORDER,

//    // And other could be
//    EXPRESS_ORDER,
//    ONCE_CLICK_ORDER,
}

enum class ProductOrderStatusV3 {
    DRAFT,

    ADDRESS_ADDED,

    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_CANCELED,

    PLACED,

    CANCELED_BY_CUSTOMER,
    CANCELED_BY_COMPANY,

    PROCESSED_BY_SELLER,

    SHIPPED,

    IN_TRANSIT,

    RETURNED_BY_DELIVERY,

    OUT_FOR_DELIVERY,

    NOT_ACCEPTED_ON_DELIVERY,

    PARTIALLY_DELIVERED, // When one or more CartItemV3 is delivered but not all of the items are delivered
    DELIVERED,

    RETURNED_BY_CUSTOMER_FOR_REPLACEMENT,
    RETURNED_BY_CUSTOMER_FOR_REFUND,

    REFUNDED_INITIATED,
    REFUNDED_SUCCESS,
    REFUND_FAILED,

    // Close the current order and start the new order with amount Rs 0.
    NEW_ORDER_STARTED_FOR_REPLACEMENT,
    REPLACED,

    ARCHIVED,


}
