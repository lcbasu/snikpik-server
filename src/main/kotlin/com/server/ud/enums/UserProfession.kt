package com.server.ud.enums

import com.server.common.enums.UserProfileType
import com.server.dk.model.MediaDetails

enum class UserProfession(
    val professionType: UserProfileType,
    val displayName: String,
    val mediaDetails: MediaDetails
) {
    ARCHITECT(
        UserProfileType.PROFESSIONAL,
        "Architect",
        MediaDetails(
            media = emptyList()
        )
    ),
    INTERIOR_DESIGNER(
        UserProfileType.PROFESSIONAL,
        "Interior Designer",
        MediaDetails(
            media = emptyList()
        )
    ),
    HOME_OWNER(
        UserProfileType.OWNER,
        "Home Owner",
        MediaDetails(
            media = emptyList()
        )
    ),
}
