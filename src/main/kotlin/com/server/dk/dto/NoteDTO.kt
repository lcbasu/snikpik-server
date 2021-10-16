package com.server.dk.dto

import com.server.dk.entities.Note
import com.server.dk.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveNoteRequest(
    val employeeId: String,
    val companyId: String,
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

fun Note.toSavedNoteResponse(): SavedNoteResponse {
    return SavedNoteResponse(
        serverId = id,
        company = company!!.toSavedCompanyResponse(),
        employee = employee!!.toSavedEmployeeResponse(),
        forDate = forDate,
        description = description ?: "",
        addedAt = DateUtils.getEpoch(addedAt))
}
