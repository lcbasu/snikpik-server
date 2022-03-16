package com.server.shop.enums

import com.server.shop.dto.DeliveryTimeIdV3Response

enum class DeliveryTimeIdV3(val rank: Int, val displayName: String)  {
    MINS_30_60(1, "30-60 mins"),
    HRS_1_4(2, "1-4 hrs"),
    HRS_6_12(3, "6-12 hrs"),
    HRS_In_24(4, "in 24 hrs"),
    DAYS_1_3(5, "1-3 days"),
    DAYS_3_5(6, "3-5 days"),
    DAYS_5_10(7, "5-10 days"),
    DAYS_10_Plus(8, "10+ days"),
}

fun DeliveryTimeIdV3.toDeliveryTimeIdV3Response(): DeliveryTimeIdV3Response {
    this.apply {
        return DeliveryTimeIdV3Response(
            id = this,
            rank = this.rank,
            displayName = this.displayName
        )
    }
}

