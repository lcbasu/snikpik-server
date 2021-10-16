package com.server.dk.service.impl

import com.server.dk.dto.SaveNoteRequest
import com.server.dk.dto.SavedNoteResponse
import com.server.dk.dto.toSavedNoteResponse
import com.server.dk.service.NoteService
import com.server.common.provider.AuthProvider
import com.server.dk.provider.NoteProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NoteServiceImpl : NoteService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var noteProvider: NoteProvider

    override fun saveNote(saveNoteRequest: SaveNoteRequest): SavedNoteResponse? {
        val requestContext = authProvider.validateRequest(
            employeeId = saveNoteRequest.employeeId,
            companyServerIdOrUsername = saveNoteRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val note = noteProvider.saveNote(requestContext.user, requestContext.company!!, requestContext.employee!!, saveNoteRequest)
        return note.toSavedNoteResponse()
    }

}
