package com.server.ud.dao.like

import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.like.LikesByResource
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikesByResourceRepository : CassandraRepository<LikesByResource?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
    @AllowFiltering
    fun findAllByResourceId(resourceId: String): List<LikesByResource>

    fun deleteAllByResourceIdAndResourceType(resourceId: String, resourceType: ResourceType)
}
