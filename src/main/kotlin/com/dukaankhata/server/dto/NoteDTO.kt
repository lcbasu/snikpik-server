package com.dukaankhata.server.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveNoteRequest(
    val employeeId: Long,
    val companyId: Long,
    val forDate: String,
    val description: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedNoteResponse(
    val serverId: String,
    val employee: SavedEmployeeResponse,
    val company: SavedCompanyResponse,
    val forDate: String,
    val addedAt: Long,
    val description: String?,
)
