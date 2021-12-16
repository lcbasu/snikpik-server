package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.bookmark.BookmarksCountByResource
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookmarksCountByResourceRepository : CassandraRepository<BookmarksCountByResource?, String?> {

//    @Query("select * from bookmarks_count_by_resource where resource_id = ?0")
    fun findAllByResourceId(resourceId: String?): List<BookmarksCountByResource>

    @Query("UPDATE bookmarks_count_by_resource SET bookmarks_count = bookmarks_count + 1 WHERE resource_id = ?0")
    fun incrementBookmarkCount(resourceId: String)

    @Query("UPDATE bookmarks_count_by_resource SET bookmarks_count = bookmarks_count - 1 WHERE resource_id = ?0")
    fun decrementBookmarkCount(resourceId: String)
}
