package com.server.dk.enums

import com.server.common.model.MediaDetails

enum class Vertical(
    val id: String,
    val displayName: String,
    val mediaDetails: MediaDetails,
    val subCategory: SubCategory
) {

}
