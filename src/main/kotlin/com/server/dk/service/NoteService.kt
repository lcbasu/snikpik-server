package com.server.dk.service

import com.server.dk.dto.SaveNoteRequest
import com.server.dk.dto.SavedNoteResponse

abstract class NoteService {
    abstract fun saveNote(saveNoteRequest: SaveNoteRequest): SavedNoteResponse?
}
