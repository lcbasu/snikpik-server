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
    val placementOrder: Int,
    val displayName: String,
    val mediaDetails: MediaDetails
) {
    EXTERIOR(
        CategoryGroupV2.HOME,
        1,
        "Exterior",
        MediaDetails(
            media = emptyList()
        )
    ),
    INTERIOR(
        CategoryGroupV2.HOME,
        1,
        "Interior",
        MediaDetails(
            media = emptyList()
        )
    ),
    KITCHEN(
        CategoryGroupV2.HOME,
        2,
        "Kitchen",
        MediaDetails(
            media = emptyList()
        )
    ),

    // Only used for filtering
    // But user should not be saving any post with category ALL
    ALL(
        CategoryGroupV2.HOME,
        0,
        "All",
        MediaDetails(
            media = emptyList()
        )
    ),
}
