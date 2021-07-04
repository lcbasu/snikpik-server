package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveNoteRequest
import com.dukaankhata.server.dto.SavedNoteResponse
import com.dukaankhata.server.dto.toSavedNoteResponse
import com.dukaankhata.server.service.NoteService
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.NoteProvider
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
            companyId = saveNoteRequest.companyId,
            requiredRoleTypes = authProvider.onlyAdminLevelRoles()
        )
        val note = noteProvider.saveNote(requestContext.user, requestContext.company!!, requestContext.employee!!, saveNoteRequest)
        return note.toSavedNoteResponse()
    }

}
