package com.dukaankhata.server.enums

enum class ProductOrderStatus {

    // Customer

    // Marked by System for Customer
    DRAFT, // Added to Cart by customer but not yet placed

    // Marked by System for Customer
    PLACED, // Placed by the customer

    // Will land here if the productOrder was modified by the Shopkeeper,
    // Customer can Accept or Reject
    // Accept -> Go to ACCEPTED_BY_CUSTOMER
    // Reject -> Go to REJECTED_BY_CUSTOMER
    PENDING_CUSTOMER_APPROVAL, // Marked by System for Customer
    // Marked by Customer
    ACCEPTED_BY_CUSTOMER,
    // Marked by Customer
    REJECTED_BY_CUSTOMER,

    // No further Action

    // Marked by Customer
    CANCELLED_BY_CUSTOMER,



    // Owner

    // Once Owner accepts the PLACED or ACCEPTED_BY_CUSTOMER ProductOrder
    // Marked by Owner
    ACCEPTED_BY_OWNER,

    // Once owner starts the delivery of the ProductOrder
    // Marked by Owner
    SHIPPED_BY_OWNER,

    // Once the productOrder is delivered to the customer
    // Marked by Owner
    DELIVERED_BY_OWNER,

    // Once the productOrder was failed to be delivered
    // Marked by Owner
    FAILED_TO_DELIVER_BY_OWNER,


    // No further Action

    // Marked by Owner
    REJECTED_BY_OWNER,
    // Marked by Owner
    CANCELLED_BY_OWNER,
    // Marked by Owner
    RETURNED_TO_OWNER, // In case the Customer refused to accepts the productOrder
}
