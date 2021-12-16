package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarksCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookmarksCountByUserRepository : CassandraRepository<BookmarksCountByUser?, String?> {

//    @Query("select * from bookmarks_count_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<BookmarksCountByUser>

    @Query("UPDATE bookmarks_count_by_user SET bookmarks_count = bookmarks_count + 1 WHERE user_id = ?0")
    fun incrementBookmarkCount(userId: String)

    @Query("UPDATE bookmarks_count_by_user SET bookmarks_count = bookmarks_count - 1 WHERE user_id = ?0")
    fun decrementBookmarkCount(userId: String)
}
