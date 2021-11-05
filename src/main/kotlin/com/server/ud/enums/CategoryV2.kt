package com.server.ud.enums

import com.server.dk.model.MediaDetails

enum class CategoryGroupV2(
    val displayName: String,
    val mediaDetails: MediaDetails
) {
    HOME(
        "Home",
        MediaDetails(
            media = emptyList()
        )
    ),
    CARS_BUY_SELL(
        "Buy and Sell Cars",
        MediaDetails(
            media = emptyList()
        )
    ),
    MOBILE_BUY_SELL(
        "Buy and Sell Mobiles",
        MediaDetails(
            media = emptyList()
        )
    ),
}
enum class CategoryV2(
    val categoryGroup: CategoryGroupV2,
    val displayName: String,
    val mediaDetails: MediaDetails
) {
    EXTERIOR(
        CategoryGroupV2.HOME,
        "Exterior",
        MediaDetails(
            media = emptyList()
        )
    ),
    KITCHEN(
        CategoryGroupV2.HOME,
        "Kitchen",
        MediaDetails(
            media = emptyList()
        )
    ),
    ALL(
        CategoryGroupV2.HOME,
        "All",
        MediaDetails(
            media = emptyList()
        )
    ),
}
