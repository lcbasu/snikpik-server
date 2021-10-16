package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.NoteService
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
