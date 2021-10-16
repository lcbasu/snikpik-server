package com.server.dk.enums

import com.server.dk.model.MediaDetails

enum class SubCategory(
    val id: String,
    val displayName: String,
    val mediaDetails: MediaDetails,
    val category: Category
) {

}
