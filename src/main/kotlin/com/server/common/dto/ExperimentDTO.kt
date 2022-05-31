package com.server.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApplicableExperimentsResponse(
    val userId: String,
    val applicableExperiments: Set<String>,
)
