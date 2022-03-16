package com.server.shop.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RazorpayOrderResponse(
    val id: String,
    val amount: Int,
    val partial_payment: Boolean,

    val amount_paid: Int,
    val amount_due: Int,

    val currency: String,
    val receipt: String,
    val status: String,

    val attempts: Int,

    val notes: Any,

    val created_at: Int,
)
