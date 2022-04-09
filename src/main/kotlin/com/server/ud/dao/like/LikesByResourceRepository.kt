package com.server.ud.dao.like

import com.server.ud.entities.like.LikesByResource
import com.server.ud.entities.like.LikesByResourceTracker
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface LikesByResourceRepository : CassandraRepository<LikesByResource?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
//    @AllowFiltering
//    fun findAllByResourceIdAndResourceType(resourceId: String, resourceType: ResourceType): List<LikesByResource>

    fun deleteAllByResourceIdAndResourceType(resourceId: String, resourceType: ResourceType)
}

@Repository
interface LikesByResourceTrackerRepository : CassandraRepository<LikesByResourceTracker?, String?> {
    fun findAllByResourceId(resourceId: String, pageable: Pageable): Slice<LikesByResourceTracker>
}
