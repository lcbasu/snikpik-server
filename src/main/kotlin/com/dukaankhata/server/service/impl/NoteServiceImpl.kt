package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveNoteRequest
import com.dukaankhata.server.dto.SavedNoteResponse
import com.dukaankhata.server.service.NoteService
import com.dukaankhata.server.service.converter.NoteServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.NoteUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NoteServiceImpl : NoteService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var noteUtils: NoteUtils

    @Autowired
    private lateinit var noteServiceConverter: NoteServiceConverter

    override fun saveNote(saveNoteRequest: SaveNoteRequest): SavedNoteResponse? {
        val requestContext = authUtils.validateRequest(
            employeeId = saveNoteRequest.employeeId,
            companyId = saveNoteRequest.companyId,
            requiredRoleTypes = authUtils.onlyAdminLevelRoles()
        )
        val note = noteUtils.saveNote(requestContext.user, requestContext.company!!, requestContext.employee!!, saveNoteRequest)
        return noteServiceConverter.getSavedNoteResponse(note)
    }

}
