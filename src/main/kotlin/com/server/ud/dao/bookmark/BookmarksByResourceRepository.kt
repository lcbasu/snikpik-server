package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarksByResourceRepository : CassandraRepository<BookmarksByResource?, String?> {

    fun findAllByResourceId(resourceId: String): List<BookmarksByResource>

    fun deleteAllByResourceId(resourceId: String)
}
