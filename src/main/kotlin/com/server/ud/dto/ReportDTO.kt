package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.enums.UserReportActionType


@JsonIgnoreProperties(ignoreUnknown = true)
data class UserReportRequest (
    val reportedByUserId: String,
    val forUserId: String,
    val reason: String?,
    val action: UserReportActionType,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class UserReportResponse (
    val reportedByUserId: String,
    val forUserId: String,
    val actionDetails: String,
    val reason: String?,
    val action: UserReportActionType,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllUserReportResponse (
    val reports: List<UserReportResponse>
)
