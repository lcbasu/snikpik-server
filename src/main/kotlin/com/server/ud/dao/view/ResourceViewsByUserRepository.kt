package com.server.ud.dao.view

import com.server.ud.entities.view.ResourceViewsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface ResourceViewsByUserRepository : CassandraRepository<ResourceViewsByUser?, String?> {
    fun findAllByUserIdAndResourceIdOrderByCreatedAt(userId: String, resourceId: String, pageable: Pageable): Slice<ResourceViewsByUser>
}
