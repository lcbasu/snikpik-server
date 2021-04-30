package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Discount
import com.dukaankhata.server.enums.DiscountType
import com.dukaankhata.server.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveDiscountRequest(
    val companyId: String,
    val promoCode: String,
    val discountType: DiscountType,
    val discountAmount: Long = 0,
    val minOrderValueInPaisa: Long? = 0,
    val maxDiscountAmountInPaisa: Long? = 0,
    val sameCustomerCount: Int = 1,
    val visibleToCustomer: Boolean = true,
    val startAt: Long,
    val endAt: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedDiscountResponse(
    val serverId: String,
    val company: SavedCompanyResponse,
    val promoCode: String,

    val discountType: DiscountType,
    val discountAmount: Long,

    val minOrderValueInPaisa: Long,
    val maxDiscountAmountInPaisa: Long,

    val sameCustomerCount: Int,
    val visibleToCustomer: Boolean,

    val startAt: Long,
    val endAt: Long,
)

fun Discount.toSavedDiscountResponse(): SavedDiscountResponse {
    this.apply {
        return SavedDiscountResponse(
            serverId = id,
            company = company!!.toSavedCompanyResponse(),
            promoCode = promoCode,
            discountType = discountType,
            discountAmount = discountAmount,
            minOrderValueInPaisa = minOrderValueInPaisa,
            maxDiscountAmountInPaisa = maxDiscountAmountInPaisa,
            sameCustomerCount = sameCustomerCount,
            visibleToCustomer = visibleToCustomer,
            startAt = DateUtils.getEpoch(startAt),
            endAt = DateUtils.getEpoch(endAt),
        )
    }
}
