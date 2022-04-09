package com.server.ud.dao.like

import com.server.ud.entities.like.LikesByUser
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface LikesByUserRepository : CassandraRepository<LikesByUser?, String?> {
//    @AllowFiltering
//    fun findAllByResourceId(resourceId: String): List<LikesByUser>

    fun deleteAllByUserIdAndCreatedAtAndResourceIdAndResourceType(userId: String, createdAt: Instant, resourceId: String, resourceType: ResourceType)
}
