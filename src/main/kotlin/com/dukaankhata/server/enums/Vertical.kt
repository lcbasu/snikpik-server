package com.dukaankhata.server.enums

import com.dukaankhata.server.model.MediaDetails

enum class Vertical(
    val id: String,
    val displayName: String,
    val mediaDetails: MediaDetails,
    val subCategory: SubCategory
) {

}
