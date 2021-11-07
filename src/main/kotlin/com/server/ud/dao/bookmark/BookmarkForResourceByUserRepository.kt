package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarkForResourceByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookmarkForResourceByUserRepository : CassandraRepository<BookmarkForResourceByUser?, String?> {
    @Query("select * from bookmark_for_resource_by_user where resource_id = ?0 and user_id = ?1")
    fun findAllByResourceAndUserId(resourceId: String, userId: String): List<BookmarkForResourceByUser>
}
