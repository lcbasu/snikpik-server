package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.NoteRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Note
import com.dukaankhata.server.service.NoteService
import com.dukaankhata.server.service.converter.NoteServiceConverter
import com.dukaankhata.server.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class NoteServiceImpl : NoteService() {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    @Autowired
    private lateinit var noteUtils: NoteUtils

    @Autowired
    private lateinit var noteServiceConverter: NoteServiceConverter

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    override fun saveNote(saveNoteRequest: SaveNoteRequest): SavedNoteResponse? {
        val punchByUser = authUtils.getRequestUserEntity()
        val company = companyUtils.getCompany(saveNoteRequest.companyId)
        val employee = employeeUtils.getEmployee(saveNoteRequest.employeeId)
        if (punchByUser == null || company == null || employee == null) {
            error("User, Company, and Employee are required to add an employee");
        }

        val userRoles = userRoleUtils.getUserRolesForUserAndCompany(
            user = punchByUser,
            company = company
        ) ?: emptyList()

        if (userRoles.isEmpty()) {
            error("Only employees of the company can add the note");
        }

        // TODO: Employee 1 can not mark note for Employee 2 unless
        val note = noteUtils.saveNote(punchByUser, company, employee, saveNoteRequest)
        return noteServiceConverter.getSavedNoteResponse(note)
    }

}
