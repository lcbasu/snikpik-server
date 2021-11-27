package com.server.ud.dao.view

import com.server.ud.entities.view.ResourceViewsByUser
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceViewsByUserRepository : CassandraRepository<ResourceViewsByUser?, String?> {
    fun findAllByUserIdAndResourceIdOrderByCreatedAt(userId: String, resourceId: String): List<ResourceViewsByUser?>?
}
