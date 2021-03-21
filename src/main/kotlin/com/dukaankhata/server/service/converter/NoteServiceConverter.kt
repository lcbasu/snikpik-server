package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedNoteResponse
import com.dukaankhata.server.entities.Note
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NoteServiceConverter {

    @Autowired
    private lateinit var companyServiceConverter: CompanyServiceConverter

    @Autowired
    private lateinit var employeeServiceConverter: EmployeeServiceConverter

    fun getSavedNoteResponse(note: Note?): SavedNoteResponse {
        return SavedNoteResponse(
            serverId = note?.id?.toString() ?: "-1",
            employee = employeeServiceConverter.getSavedEmployeeResponse(note?.employee),
            company = companyServiceConverter.getSavedCompanyResponse(note?.company),
            forDate = note?.forDate ?: "",
            description = note?.description ?: "",
            addedAt = DateUtils.getEpoch(note?.addedAt))
    }

}
