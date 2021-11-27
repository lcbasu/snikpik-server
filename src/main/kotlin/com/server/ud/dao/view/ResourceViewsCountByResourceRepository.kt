package com.server.ud.dao.view

import com.server.ud.entities.view.ResourceViewsCountByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ResourceViewsCountByResourceRepository : CassandraRepository<ResourceViewsCountByResource?, String?> {

    fun findAllByResourceId(resourceId: String?): List<ResourceViewsCountByResource>

    @Query("UPDATE resource_views_count_by_resource SET views_count = views_count + 1 WHERE resource_id = ?0")
    fun incrementResourceViewCount(resourceId: String)

    @Query("UPDATE resource_views_count_by_resource SET views_count = views_count - 1 WHERE resource_id = ?0")
    fun decrementResourceViewCount(resourceId: String)
}
