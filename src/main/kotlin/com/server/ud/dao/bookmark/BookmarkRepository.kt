package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.Bookmark
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : CassandraRepository<Bookmark?, String?> {
//    @Query("select * from bookmarks where bookmark_id = ?0")
    fun findAllByBookmarkId(bookmarkId: String?): List<Bookmark>

//    @AllowFiltering
//    fun findAllByResourceId(resourceId: String): List<Bookmark>

    fun deleteByBookmarkId(bookmarkId: String)

//    @AllowFiltering
//    @Query("SELECT * FROM bookmarks")
//    fun getAll(): List<Bookmark>
}
