package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.NoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("note")
class NoteController {
    @Autowired
    private lateinit var noteService: NoteService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveNoteRequest: SaveNoteRequest): SavedNoteResponse? {
        return noteService.saveNote(saveNoteRequest)
    }
}
