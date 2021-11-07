package com.server.common.enums

import com.server.dk.model.MediaDetailsV2

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
    HARDWARE_STORE_OWNER(
        ProfileCategory.SUPPLIER,
        "Hardware Store Owner",
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
