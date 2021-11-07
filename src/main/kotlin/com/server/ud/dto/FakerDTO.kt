package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakerRequest(
    var countOfPost: Int,
    var maxCountOfComments: Int,
    var maxCountOfReplies: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakerResponse(
    var result: List<Any>
)

