package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : JpaRepository<Note?, Long?>
