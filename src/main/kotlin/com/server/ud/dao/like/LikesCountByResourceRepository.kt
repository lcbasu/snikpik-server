package com.server.ud.dao.like

import com.server.ud.entities.like.LikesCountByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LikesCountByResourceRepository : CassandraRepository<LikesCountByResource?, String?> {

    @Query("select * from likes_count_by_resource where resource_id = ?0")
    fun findAllByResourceId(resourceId: String?): List<LikesCountByResource>

    @Query("UPDATE likes_count_by_resource SET likes_count = likes_count + 1 WHERE resource_id = ?0")
    fun incrementLikes(resourceId: String)

    @Query("UPDATE likes_count_by_resource SET likes_count = likes_count - 1 WHERE resource_id = ?0")
    fun decrementLikes(resourceId: String)
}
