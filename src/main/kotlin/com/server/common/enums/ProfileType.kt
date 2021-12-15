package com.server.common.enums

import com.server.common.model.MediaDetailsV2

enum class ProfileType(
    val category: ProfileCategory,
    val displayName: String,
    val media: MediaDetailsV2
) {
    ARCHITECT(
        ProfileCategory.PROFESSIONAL,
        "Architect",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    INTERIOR_DESIGNER(
        ProfileCategory.PROFESSIONAL,
        "Interior Designer",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    MASON(
        ProfileCategory.PROFESSIONAL,
        "Mason",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    CARPENTER(
        ProfileCategory.PROFESSIONAL,
        "Carpenter",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    HARDWARE_STORE_OWNER(
        ProfileCategory.SUPPLIER,
        "Hardware Store Owner",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    PLUMBER_SUPPLIER(
        ProfileCategory.SUPPLIER,
        "Plumber Supplier",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    PAINT_WHOLE_SELLER(
        ProfileCategory.SUPPLIER,
        "Paint Whole Seller",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    HOME_OWNER(
        ProfileCategory.OWNER,
        "Home Owner",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
}
