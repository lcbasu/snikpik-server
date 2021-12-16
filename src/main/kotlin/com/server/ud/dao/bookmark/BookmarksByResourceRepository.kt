package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksByResource
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarksByResourceRepository : CassandraRepository<BookmarksByResource?, String?> {
    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<BookmarksByResource>
}
