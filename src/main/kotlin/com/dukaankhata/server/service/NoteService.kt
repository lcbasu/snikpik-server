package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveNoteRequest
import com.dukaankhata.server.dto.SavedNoteResponse

abstract class NoteService {
    abstract fun saveNote(saveNoteRequest: SaveNoteRequest): SavedNoteResponse?
}
