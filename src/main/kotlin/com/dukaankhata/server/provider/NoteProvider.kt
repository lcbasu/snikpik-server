package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.NoteRepository
import com.dukaankhata.server.dto.SaveNoteRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Note
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.utils.DateUtils
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
