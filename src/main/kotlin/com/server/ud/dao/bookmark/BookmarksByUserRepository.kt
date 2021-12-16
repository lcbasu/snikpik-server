package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksByUser
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarksByUserRepository : CassandraRepository<BookmarksByUser?, String?> {
    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<BookmarksByUser>
}
