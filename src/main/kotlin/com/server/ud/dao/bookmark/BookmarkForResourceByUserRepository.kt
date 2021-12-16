package com.server.ud.dao.bookmark

import com.server.ud.entities.bookmark.BookmarkForResourceByUser
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkForResourceByUserRepository : CassandraRepository<BookmarkForResourceByUser?, String?> {
//    @Query("select * from bookmark_for_resource_by_user where resource_id = ?0 and user_id = ?1")
    fun findAllByResourceIdAndUserId(resourceId: String, userId: String): List<BookmarkForResourceByUser>

    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<BookmarkForResourceByUser>
}
