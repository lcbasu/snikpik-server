package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.NoteRepository
import com.server.dk.dto.SaveNoteRequest
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.Note
import com.server.dk.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NoteProvider {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun saveNote(addedBy: User, company: Company, employee: Employee, saveNoteRequest: SaveNoteRequest): Note {
        val newNote = Note()
        newNote.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.NTE.name)
        newNote.forDate = saveNoteRequest.forDate
        newNote.addedBy = addedBy
        newNote.addedAt = DateUtils.dateTimeNow()
        newNote.description = saveNoteRequest.description
        newNote.employee = employee
        newNote.company = company
        return noteRepository.save(newNote)

    }

}
