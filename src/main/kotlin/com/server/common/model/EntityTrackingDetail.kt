package com.server.common.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class TrackingData(
    val data: Map<String, Set<String>>
)

fun TrackingData.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}
