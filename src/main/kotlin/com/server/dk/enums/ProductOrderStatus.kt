package com.server.dk.enums

enum class ProductOrderStatus {

    // Customer

    // Marked by System for Customer
    DRAFT, // Added to Cart by customer but not yet placed

    // Marked by System for Customer
    ADDRESS_CONFIRMED, // New Address added or old address is confirmed by the seller

    PAYMENT_CONFIRMED, // COD selected or Online Payment is done

    // Marked by System for Customer
    PLACED, // Placed by the customer

    // Will land here if the productOrder was modified by the Shopkeeper,
    // Customer can Accept or Reject
    // Accept -> Go to ACCEPTED_BY_CUSTOMER
    // Reject -> Go to REJECTED_BY_CUSTOMER
    PENDING_CUSTOMER_APPROVAL, // Marked by System for Customer whenever the Seller modifies the order like Delivery Cost for example
    // Marked by Customer
    ACCEPTED_BY_CUSTOMER,
    // Marked by Customer
    REJECTED_BY_CUSTOMER,

    // No further Action

    // Marked by Customer
    CANCELLED_BY_CUSTOMER,



    // Seller

    PENDING_SELLER_APPROVAL, // Marked by System for Seller whenever the Customer modifies the order like Address for example

    // Once Seller accepts the PLACED or ACCEPTED_BY_CUSTOMER ProductOrder
    // Marked by Seller
    ACCEPTED_BY_SELLER,

    // Once owner starts the delivery of the ProductOrder
    // Marked by Seller
    SHIPPED_BY_SELLER,

    // Once the productOrder is delivered to the customer
    // Marked by Seller
    DELIVERED_BY_SELLER,

    // Once the productOrder was failed to be delivered
    // Marked by Seller
    FAILED_TO_DELIVER_BY_SELLER,


    // No further Action

    /**
     *
     * What is the difference between REJECTED_BY_SELLER and CANCELLED_BY_SELLER
     *
     * They seem like same thing.
     *
     * REJECTED_BY_CUSTOMER & CANCELLED_BY_CUSTOMER makes sense
     * REJECTED_BY_CUSTOMER -> Customer Rejected the modifications made by the seller
     * CANCELLED_BY_CUSTOMER -> Customer just wants to cancel the order
     *
     *
     * But there are no scenario where CANCELLED_BY_SELLER would happen a
     *
     *
     * */

    // Marked by Seller
    REJECTED_BY_SELLER,
    // Marked by Seller
    CANCELLED_BY_SELLER,
    // Marked by Seller
    RETURNED_TO_OWNER, // In case the Customer refused to accepts the productOrder
}
